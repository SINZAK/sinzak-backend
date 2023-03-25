package net.sinzak.server.chatroom.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.chatroom.domain.ChatRoom;
import net.sinzak.server.chatroom.domain.UserChatRoom;
import net.sinzak.server.chatroom.dto.request.PostDto;
import net.sinzak.server.chatroom.dto.respond.GetCreatedChatRoomDto;
import net.sinzak.server.chatroom.repository.ChatRoomRepository;
import net.sinzak.server.chatroom.repository.UserChatRoomRepository;
import net.sinzak.server.common.PostType;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.error.ChatRoomNotFoundException;
import net.sinzak.server.common.error.PostNotFoundException;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.image.S3Service;
import net.sinzak.server.product.domain.Product;
import net.sinzak.server.product.repository.ProductRepository;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.repository.UserRepository;
import net.sinzak.server.user.service.UserQueryService;
import net.sinzak.server.work.domain.Work;
import net.sinzak.server.work.repository.WorkRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatRoomCommandService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final WorkRepository workRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final UserQueryService userQueryService;

    public JSONObject createUserChatRoom(PostDto postDto, User user) { //상대방 아바타를 초대
        User postUser =null;
        List<ChatRoom> postChatRooms = null;
        Product product = null;
        Work work = null;
        if(postDto.getPostType().equals(PostType.PRODUCT.getName())){
            Optional<Product> findProduct = productRepository.findByIdFetchChatRooms(postDto.getPostId());
            if(findProduct.isEmpty()){
                throw new PostNotFoundException();
            }
            postUser = findProduct.get().getUser();
            postChatRooms = findProduct.get().getChatRooms(); //여기서 채팅방을 나중에 가져오는 것도 고려
            product = findProduct.get();
        }
        if(postDto.getPostType().equals(PostType.WORK.getName())){
            Optional<Work> findWork = workRepository.
                    findByIdFetchChatRooms(postDto.getPostId());
            if(findWork.isEmpty()){
                throw new PostNotFoundException();
            }
            postUser = findWork.get().getUser();
            postChatRooms = findWork.get().getChatRooms();
            work = findWork.get();
        }

        checkUserStatus(user,postUser);
        User loginUser = userRepository.findByIdNotDeleted(user.getId()).orElseThrow(UserNotFoundException::new);

        if(userQueryService.checkReported(postUser,user)){
            return PropertyUtil.responseMessage("차단된 상대입니다.");
        }

        GetCreatedChatRoomDto getCreatedChatRoomDto =new GetCreatedChatRoomDto();
        ChatRoom chatRoom = checkIfUserIsAlreadyChatting(user, postChatRooms);
        if (chatRoom == null) { //상대랑 해당 포스트에 대해서 대화하고 있는 채팅방이 없다면 (만들어 줘야함)
            chatRoom = makeChatRoomAndUserChatRoom(postUser, loginUser);
            addChatRoomToPost(postDto, product, work, chatRoom);
            chatRoomRepository.save(chatRoom);
            getCreatedChatRoomDto.setNewChatRoom(true);
        }
        else{
            getCreatedChatRoomDto.setNewChatRoom(false);
        }
        getCreatedChatRoomDto.setRoomUuid(chatRoom.getRoomUuid());
        return PropertyUtil.response(getCreatedChatRoomDto);
    }
    private void addChatRoomToPost(PostDto postDto, Product product, Work work, ChatRoom chatRoom) {
        if(postDto.getPostType().equals(PostType.WORK.getName())){
            work.addChatRoom(chatRoom);
        }
        if(postDto.getPostType().equals(PostType.PRODUCT.getName())){
            product.addChatRoom(chatRoom);
        }
    }
    public JSONObject uploadImage(String roomUuid, List<MultipartFile> multipartFiles){
        Optional<ChatRoom> chatRoom = chatRoomRepository.findByRoomId(roomUuid);
        List<JSONObject> obj = new ArrayList<>();
        if(chatRoom.isEmpty()){
            throw new ChatRoomNotFoundException();
        }
        for(MultipartFile multipartFile : multipartFiles ){
            JSONObject jsonObject = new JSONObject();
            String url = s3Service.uploadImage(multipartFile);
            jsonObject.put("url",url);
            obj.add(jsonObject);
        }
        return PropertyUtil.response(obj);
    }
//    public JSONObject leaveChatRoom(User user,String roomUuid){
//        ChatRoom findChatRoom = chatRoomRepository.findByRoomId(roomUuid).orElseThrow(()->new InstanceNotFoundException("존재하지 않는 채팅방입니다."));
//        List<UserChatRoom> userCha
//
//    }

    @NotNull
    private ChatRoom makeChatRoomAndUserChatRoom(User postUser, User loginUser) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setPostUserId(postUser.getId());
        UserChatRoom myUserChatRoom = new UserChatRoom(loginUser, postUser);
        UserChatRoom OpponentUserChatRoom = new UserChatRoom(postUser, loginUser);
        chatRoom.addUserChatRoom(myUserChatRoom);
        chatRoom.addUserChatRoom(OpponentUserChatRoom);
        userChatRoomRepository.save(myUserChatRoom);
        userChatRoomRepository.save(OpponentUserChatRoom);
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }
    private GetCreatedChatRoomDto makeChatRoomDto( ChatRoom chatRoom) {
        return GetCreatedChatRoomDto.builder()
                .roomUuid(chatRoom.getRoomUuid())
                .build();
    }

    public void makeChatRoomBlocked(User user,User opponentUser,boolean isBlock){
        List<UserChatRoom> userChatRooms = userChatRoomRepository.findUserChatRoomByIdFetchChatRoom(user.getId());
        for(UserChatRoom userChatRoom : userChatRooms){
            if(userChatRoom.getOpponentUserId().equals(opponentUser.getId())){
                userChatRoom.getChatRoom().setBlocked(isBlock);
            }
        }
    }
    private ChatRoom checkIfUserIsAlreadyChatting(User user, List<ChatRoom> postChatRooms) {
        for(ChatRoom chatRoom: postChatRooms){
            for(UserChatRoom userChatRoom : chatRoom.getUserChatRooms()){
                //Post에 딸린 채팅방중 말 건 유저가 속한 채팅방이 있다면
                if(userChatRoom.getUser().getId().equals(user.getId())){
                    if(userChatRoom.isDisable()){
                        chatRoom.reEnterChatRoom(); //참여자 수 올려줌
                        userChatRoom.setDisable(false);
                    }
                    return chatRoom;
                }
            }
        }
        return null;
    }
    @Nullable
    private void checkUserStatus(User user, User postUser) {
        if (user == null ||postUser ==null) {
            throw new UserNotFoundException();
        }
    }

}

