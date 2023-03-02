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
import net.sinzak.server.common.error.InstanceNotFoundException;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.common.error.UserNotLoginException;
import net.sinzak.server.product.domain.Product;
import net.sinzak.server.product.service.ProductService;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.work.domain.Work;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ChatRoomQueryService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final ProductService productService;
    private final WorkService workService;

    public JSONObject getChatRoomsByPost(User loginUser, PostDto postDto){
        if(loginUser ==null){
            throw new UserNotLoginException();
        }
        List<GetChatRoomsDto> getChatRoomsDtos = new ArrayList<>();
        List<UserChatRoom> userChatRooms = userChatRoomRepository.findUserChatRoomByIdFetchChatRoomWhereNotDisabled(loginUser.getId());
        for(UserChatRoom userChatRoom :userChatRooms){
            ChatRoom chatRoom = userChatRoom.getChatRoom();
            if(chatRoom.getPostType().getName().equals(postDto.getPostType())){
                GetChatRoomsDto getChatRoomsDto =
                        GetChatRoomsDto.builder()
                                .roomUuid(chatRoom.getRoomUuid())
                                .roomName(userChatRoom.getRoomName())
                                .image(userChatRoom.getImage())
                                .univ(userChatRoom.getOpponentUserUniv())
                                .latestMessage(userChatRoom.getLatestMessage())
                                .latestMessageTime(userChatRoom.getLatestMessageTime())
                                .build();
                getChatRoomsDtos.add(getChatRoomsDto);
            }
        }
        return PropertyUtil.response(getChatRoomsDtos);
          // 포스트기반 찾기
//        List<ChatRoom> chatRooms =null;
//        if(postDto.getPostType().equals(PostType.WORK.name())){
//            chatRooms = workService.getChatRoom(postDto.getPostId());
//        }
//        if(postDto.getPostType().equals(PostType.PRODUCT.name())) {
//            chatRooms = productService.getChatRoom(postDto.getPostId());
//        }
//        for(ChatRoom chatRoom: chatRooms){
//            Set<UserChatRoom> userChatRooms = chatRoom.getUserChatRooms();
//            for(UserChatRoom userChatRoom : userChatRooms){
//                if(!userChatRoom.getOpponentUserId().equals(loginUser.getId())){
//                    //유저 채팅방의 상대방 id가 내 Id와 다르다면
//                    GetChatRoomDto getChatRoomDto =null;
//                    if(postDto.getPostType().equals(PostType.WORK.name())){
//
//                    }
//                    if(postDto.getPostType().equals(PostType.PRODUCT.name())) {
//
//                    }
//                }
//            }
//        }
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
                .orElseThrow(()->new ChatRoomNotFoundException());
        List<GetChatMessageDto> getChatMessageDtos = findChatRoom.getChatMessages()
                .stream()
                .sorted(Comparator.comparing(ChatMessage::getId).reversed())
                .map(
                chatMessage -> GetChatMessageDto.builder()
                        .senderName(chatMessage.getSenderName())
                        .messageId(chatMessage.getId())
                        .sendAt(chatMessage.getCreatedDate())
                        .message(chatMessage.getMessage())
                        .senderId(chatMessage.getSenderId())
                        .build()
        ).collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min((start+pageable.getPageSize()),getChatMessageDtos.size());
        return new PageImpl<>(getChatMessageDtos.subList(start,end)
                ,pageable
                ,getChatMessageDtos.size());
    }

    public JSONObject getChatRoom(String roomUuid,User user){
        if(user ==null){
            return PropertyUtil.responseMessage(UserNotFoundException.USER_NOT_LOGIN);
        }
        List<UserChatRoom> userChatRooms = userChatRoomRepository
                .findUserChatRoomByIdFetchChatRoom(user.getId());
        for(UserChatRoom userChatRoom : userChatRooms){
            if(userChatRoom.getChatRoom().getRoomUuid().equals(roomUuid)){
                ChatRoom chatRoom = userChatRoom.getChatRoom();
                if(chatRoom.getPostType().equals(PostType.PRODUCT)){
                    GetChatRoomDto getChatRoomDto = makeProductChatRoomDto(userChatRoom, chatRoom);
                    return PropertyUtil.response(getChatRoomDto);
                }
                if(chatRoom.getPostType().equals(PostType.WORK)){
                    GetChatRoomDto getChatRoomDto = makeWorkChatRoomDto(userChatRoom, chatRoom);
                    return PropertyUtil.response(getChatRoomDto);
                }
            }
        }
        throw new ChatRoomNotFoundException();
    }

    private GetChatRoomDto makeWorkChatRoomDto(UserChatRoom userChatRoom, ChatRoom chatRoom) {
        GetChatRoomDto getChatRoomDto = GetChatRoomDto.builder()
                .userId(chatRoom.getPostUserId())
                .roomName(userChatRoom.getRoomName())
                .productId(chatRoom.getWork().getId())
                .productName(chatRoom.getWork().getTitle())
                .price(chatRoom.getWork().getPrice())
                .thumbnail(chatRoom.getWork().getThumbnail())
                .complete(chatRoom.getWork().isComplete())
                .suggest(chatRoom.getWork().isSuggest())
                .build();
        return getChatRoomDto;
    }

    private GetChatRoomDto makeProductChatRoomDto(UserChatRoom userChatRoom, ChatRoom chatRoom) {
        GetChatRoomDto getChatRoomDto = GetChatRoomDto.builder()
                .userId(chatRoom.getPostUserId())
                .roomName(userChatRoom.getRoomName())
                .productId(chatRoom.getProduct().getId())
                .productName(chatRoom.getProduct().getTitle())
                .price(chatRoom.getProduct().getPrice())
                .thumbnail(chatRoom.getProduct().getThumbnail())
                .complete(chatRoom.getProduct().isComplete())
                .suggest(chatRoom.getProduct().isSuggest())
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
