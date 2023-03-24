package net.sinzak.server.chatroom.service;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.chatroom.domain.ChatMessage;
import net.sinzak.server.chatroom.domain.ChatRoom;
import net.sinzak.server.chatroom.domain.UserChatRoom;
import net.sinzak.server.chatroom.dto.request.PostDto;
import net.sinzak.server.chatroom.dto.respond.GetChatMessageDto;
import net.sinzak.server.chatroom.dto.respond.GetChatRoomDto;
import net.sinzak.server.chatroom.dto.respond.GetChatRoomsDto;
import net.sinzak.server.chatroom.repository.ChatRoomRepository;
import net.sinzak.server.chatroom.repository.UserChatRoomRepository;
import net.sinzak.server.common.PostType;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.error.ChatRoomNotFoundException;
import net.sinzak.server.common.error.UserNotLoginException;
import net.sinzak.server.product.service.ProductService;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.work.service.WorkService;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ChatRoomQueryService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;

    public JSONObject getChatRoomsByPost(User loginUser, PostDto postDto){
        if(loginUser == null){
            throw new UserNotLoginException();
        }
        List<GetChatRoomsDto> getChatRoomsDtos = new ArrayList<>();
        List<UserChatRoom> userChatRooms = userChatRoomRepository.findUserChatRoomByUserIdWhereNotDisabled(loginUser.getId());
        List<ChatRoom> postChatRoom = null;
        if(postDto.getPostType().equals(PostType.PRODUCT.getName())){
            postChatRoom = chatRoomRepository.findChatRoomByProductId(postDto.getPostId());
            log.info(postChatRoom.size() +":딸린 채팅방 개수");
            for(UserChatRoom userChatRoom :userChatRooms){
                log.info(userChatRooms.size()+":내 모든 채팅방 개수");
                for(ChatRoom chatRoom : postChatRoom){
                    if(userChatRoom.getChatRoom().getId().equals(chatRoom.getId())){
                        GetChatRoomsDto getChatRoomsDto = makeUserChatRoom(userChatRoom, chatRoom);
                        getChatRoomsDtos.add(getChatRoomsDto);
                    }
                }
            }
        }
        if(postDto.getPostType().equals(PostType.WORK.getName())){
            postChatRoom = chatRoomRepository.findChatRoomByWorkId(postDto.getPostId());
            for(UserChatRoom userChatRoom :userChatRooms){
                for(ChatRoom chatRoom : postChatRoom){
                    if(userChatRoom.getChatRoom().getId().equals(chatRoom.getId())){
                        GetChatRoomsDto getChatRoomsDto = makeUserChatRoom(userChatRoom, chatRoom);
                        getChatRoomsDtos.add(getChatRoomsDto);
                    }
                }
            }
        }
        return PropertyUtil.response(getChatRoomsDtos);
    }

    private GetChatRoomsDto makeUserChatRoom(UserChatRoom userChatRoom, ChatRoom chatRoom) {
        return GetChatRoomsDto.builder()
                .roomUuid(chatRoom.getRoomUuid())
                .roomName(userChatRoom.getRoomName())
                .image(userChatRoom.getImage())
                .univ(userChatRoom.getOpponentUserUniv())
                .latestMessage(userChatRoom.getLatestMessage())
                .latestMessageTime(userChatRoom.getLatestMessageTime())
                .build();
    }

    public JSONObject getChatRooms(User user){
        List<GetChatRoomsDto> chatRoomsDtos = userChatRoomRepository
                .findUserChatRoomByIdFetchChatRoomWhereNotDisabled(user.getId()).stream()
                .map(
                        userChatRoom ->
                                GetChatRoomsDto.builder()
                                        .roomName(userChatRoom.getRoomName())
                                        .image(userChatRoom.getImage())
                                        .univ(userChatRoom.getOpponentUserUniv())
                                        .roomUuid(userChatRoom.getChatRoom().getRoomUuid())
                                        .latestMessage(userChatRoom.getLatestMessage())
                                        .latestMessageTime(userChatRoom.getLatestMessageTime())
                                        .build()
                )
                .collect(Collectors.toList());
        return PropertyUtil.response(chatRoomsDtos);
    }
    public Page<GetChatMessageDto> getChatRoomMessage(String roomUuid, Pageable pageable){
        ChatRoom findChatRoom = chatRoomRepository.findByRoomUuidFetchChatMessage(roomUuid)
                .orElseThrow(ChatRoomNotFoundException::new);
        List<GetChatMessageDto> getChatMessageDtos = findChatRoom.getChatMessages()
                .stream()
                .sorted(Comparator.comparing(ChatMessage::getId).reversed())
                .map(
                chatMessage -> GetChatMessageDto.builder()
                        .messageId(chatMessage.getId())
                        .senderName(chatMessage.getSenderName())
                        .sendAt(chatMessage.getCreatedDate())
                        .message(chatMessage.getMessage())
                        .senderId(chatMessage.getSenderId())
                        .messageType(chatMessage.getType().toString())
                        .build()
        ).collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min((start+pageable.getPageSize()),getChatMessageDtos.size());
        return new PageImpl<>(getChatMessageDtos.subList(start,end)
                ,pageable
                ,getChatMessageDtos.size());
    }

    public JSONObject getChatRoom(String roomUuid,User user){
        ChatRoom chatRoom = chatRoomRepository.findByRoomUuidFetchUserChatRoom(roomUuid).orElseThrow(ChatRoomNotFoundException::new);
        UserChatRoom myUserChatRoom =null;
        UserChatRoom opponentUserChatRoom = null;
        for(UserChatRoom userChatRoom : chatRoom.getUserChatRooms()){
            if(userChatRoom.getUser().getId().equals(user.getId())){
                myUserChatRoom =  userChatRoom;
            }
            else{
                opponentUserChatRoom = userChatRoom;
            }
        }
        if(chatRoom.getPostType().equals(PostType.PRODUCT)){
            GetChatRoomDto getChatRoomDto = makeProductChatRoomDto(myUserChatRoom,chatRoom,opponentUserChatRoom);
            if(chatRoom.getProduct().isDeleted()){
                getChatRoomDto.setPostId(null);
            }
            return PropertyUtil.response(getChatRoomDto);
        }
        if(chatRoom.getPostType().equals(PostType.WORK)){
            GetChatRoomDto getChatRoomDto = makeWorkChatRoomDto(myUserChatRoom,chatRoom,opponentUserChatRoom);
            if(chatRoom.getWork().isDeleted()){
                getChatRoomDto.setPostId(null);
            }
            return PropertyUtil.response(getChatRoomDto);
        }
        return PropertyUtil.responseMessage("잘못된 요청입니다");
    }

    private GetChatRoomDto makeWorkChatRoomDto(UserChatRoom userChatRoom, ChatRoom chatRoom,UserChatRoom opponentUserChatRoom) {
        GetChatRoomDto getChatRoomDto = GetChatRoomDto.builder()
                .postType(PostType.WORK)
                .postUserId(chatRoom.getPostUserId())
                .roomName(userChatRoom.getRoomName())
                .postId(chatRoom.getWork().getId())
                .postType(chatRoom.getPostType())
                .postName(chatRoom.getWork().getTitle())
                .price(chatRoom.getWork().getPrice())
                .thumbnail(chatRoom.getWork().getThumbnail())
                .complete(chatRoom.getWork().isComplete())
                .suggest(chatRoom.getWork().isSuggest())
                .opponentUserId(opponentUserChatRoom.getUser().getId())
                .build();
        return getChatRoomDto;
    }

    private GetChatRoomDto makeProductChatRoomDto(UserChatRoom myUserChatRoom, ChatRoom chatRoom,UserChatRoom opponentUserChatRoom) {
        GetChatRoomDto getChatRoomDto = GetChatRoomDto.builder()
                .postType(PostType.PRODUCT)
                .postUserId(chatRoom.getPostUserId())
                .roomName(myUserChatRoom.getRoomName())
                .postId(chatRoom.getProduct().getId())
                .postType(chatRoom.getPostType())
                .postName(chatRoom.getProduct().getTitle())
                .price(chatRoom.getProduct().getPrice())
                .thumbnail(chatRoom.getProduct().getThumbnail())
                .complete(chatRoom.getProduct().isComplete())
                .suggest(chatRoom.getProduct().isSuggest())
                .opponentUserId(opponentUserChatRoom.getUser().getId())
                .build();
        return getChatRoomDto;
    }

//    public List<ChatRoomDto> getChatRooms(User user){
//        List<UserChatRoom> userChatRooms = userChatRoomRepository.findUserChatRoomBySessionUserEmail(user.getEmail());
//        List<ChatRoomDto> chatRoomDtos =new ArrayList<>();
//        for(UserChatRoom userChatRoom: userChatRooms){
//            ChatRoomDto chatRoomDto = makeChatRoomDto(userChatRoom);
//            chatRoomDtos.add(chatRoomDto);
//        }
//        return chatRoomDtos;
//    }
//    private ChatRoomDto makeChatRoomDto(UserChatRoom userChatRoom) {
//        ChatRoomDto chatRoomDto = ChatRoomDto.builder()
//                .image(userChatRoom.getImage())
//                .roomName(userChatRoom.getRoomName())
//                .uuid(userChatRoom.getChatRoom().getUuid())
//                .build();
//        return chatRoomDto;
//    }
}
