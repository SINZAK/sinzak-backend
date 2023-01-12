package net.sinzak.server.chatroom.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.chatroom.domain.ChatRoom;
import net.sinzak.server.chatroom.domain.UserChatRoom;
import net.sinzak.server.chatroom.dto.request.PostDto;
import net.sinzak.server.chatroom.dto.respond.GetChatRoomDto;
import net.sinzak.server.chatroom.repository.ChatRoomRepository;
import net.sinzak.server.chatroom.repository.UserChatRoomRepository;
import net.sinzak.server.common.PostType;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.product.domain.Product;
import net.sinzak.server.product.repository.ProductRepository;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.repository.UserRepository;
import net.sinzak.server.work.domain.Work;
import net.sinzak.server.work.repository.WorkRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public JSONObject createUserChatRoom(PostDto postDto, User user) { //상대방 아바타를 초대
        User postUser =null;
        List<ChatRoom> postChatRooms = null;
        Product product = null;
        Work work = null;
        if(postDto.getPostType().equals(PostType.PRODUCT.getName())){
            Optional<Product> findProduct = productRepository.findByIdFetchUserAndChatRooms(postDto.getPostId());
            if(!findProduct.isPresent()){
                return PropertyUtil.responseMessage("없는 게시글입니다.");
            }
            postUser = findProduct.get().getUser();
            postChatRooms = findProduct.get().getChatRooms();
            product = findProduct.get();
        }
        if(postDto.getPostType().equals(PostType.WORK.getName())){
            Optional<Work> findWork = workRepository.findByIdFetchUserAAndChatRooms(postDto.getPostId());
            if(!findWork.isPresent()){
                return PropertyUtil.responseMessage("없는 게시글입니다.");
            }
            postUser = findWork.get().getUser();
            postChatRooms = findWork.get().getChatRooms();
            work = findWork.get();
        }
        JSONObject userStatus = checkUserStatus(user, postUser);
        if (userStatus != null) return userStatus; //만약 로그인 안 되어있거나 상대가 없다면
        User findUser = userRepository.findByEmail(user.getEmail()).get();

        ChatRoom chatRoom = checkIfUserIsAlreadyChatting(user, postChatRooms);
        if (chatRoom == null) { //상대랑 해당 포스트에 대해서 대화하고 있는 채팅방이 없다면 (만들어 줘야함)
            chatRoom = makeChatRoomAndUserChatRoom(postUser, findUser);
            addChatRoomToPost(postDto, product, work, chatRoom);
        }
        GetChatRoomDto getChatRoomDto = makeChatRoomDto(postUser, chatRoom);
        return PropertyUtil.response(getChatRoomDto);
    }
    private void addChatRoomToPost(PostDto postDto, Product product, Work work, ChatRoom chatRoom) {
        if(postDto.getPostType().equals(PostType.WORK.getName())){
            work.addChatRoom(chatRoom);
        }
        if(postDto.getPostType().equals(PostType.PRODUCT.getName())){
            product.addChatRoom(chatRoom);
        }
    }

    @NotNull
    private ChatRoom makeChatRoomAndUserChatRoom(User invitedUser, User findUser) {
        ChatRoom chatRoom = new ChatRoom();
        UserChatRoom myUserChatRoom = new UserChatRoom(findUser, invitedUser);
        UserChatRoom OpponentUserChatRoom = new UserChatRoom(invitedUser, findUser);
        chatRoom.addUserChatRoom(myUserChatRoom);
        chatRoom.addUserChatRoom(OpponentUserChatRoom);
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }
    private GetChatRoomDto makeChatRoomDto(User PostUser, ChatRoom chatRoom) {
        GetChatRoomDto getChatRoomDto = GetChatRoomDto.builder()
                .roomName(PostUser.getName())
                .uuid(chatRoom.getRoomUuid())
                .image(PostUser.getPicture())
                .postType(chatRoom.getPostType().getName())
                .build();
        return getChatRoomDto;
    }

    private ChatRoom checkIfUserIsAlreadyChatting(User user, List<ChatRoom> postChatRooms) {
        for(ChatRoom chatRoom: postChatRooms){
            for(UserChatRoom userChatRoom : chatRoom.getUserChatRooms()){
                //Post에 딸린 채팅방중 말 건 유저가 속한 채팅방이 있다면
                if(userChatRoom.getOpponentUserEmail().equals(user.getEmail())){
                    return chatRoom;
                }
            }
        }
        return null;
    }
    @Nullable
    private JSONObject checkUserStatus(User user, User postUser) {
        if (user == null) {
            return PropertyUtil.responseMessage(UserNotFoundException.USER_NOT_LOGIN);
        }
        if (postUser ==null) {
            return PropertyUtil.responseMessage(UserNotFoundException.USER_NOT_FOUND);
        }
        return null;
    }

    ///밑은 타임리프 테스트
    public void makeChatRoom(String roomName) {
        ChatRoom chatRoom = new ChatRoom(roomName);
        chatRoomRepository.save(chatRoom);
    }

    private ChatRoom checkIfChatRoomExist(User postUser, User findUser, ChatRoom chatRoom) {

        List<UserChatRoom> userChatRooms =
                userChatRoomRepository.findUserChatRoomByEmail(findUser.getEmail());
        chatRoom = getChatRoom(postUser, userChatRooms, chatRoom);
        return chatRoom;
    }
    private ChatRoom getChatRoom(User PostUser, List<UserChatRoom> userChatRooms, ChatRoom chatRoom) {
        for (UserChatRoom userChatRoom : userChatRooms) {
            if (userChatRoom.getOpponentUserEmail().equals(PostUser.getEmail())) { //만약에 이미 상대랑 같이 메시지하고 있는 방이 있다면
                chatRoom = userChatRoom.getChatRoom();
                break;
            }
        }
        return chatRoom;
    }

}

