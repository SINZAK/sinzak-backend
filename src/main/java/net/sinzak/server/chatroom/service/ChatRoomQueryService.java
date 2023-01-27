package net.sinzak.server.chatroom.service;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.chatroom.domain.ChatRoom;
import net.sinzak.server.chatroom.domain.UserChatRoom;
import net.sinzak.server.chatroom.dto.respond.GetChatMessageDto;
import net.sinzak.server.chatroom.dto.respond.GetChatRoomDto;
import net.sinzak.server.chatroom.dto.respond.GetChatRoomsDto;
import net.sinzak.server.chatroom.repository.ChatRoomRepository;
import net.sinzak.server.chatroom.repository.UserChatRoomRepository;
import net.sinzak.server.common.PostType;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.error.InstanceNotFoundException;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.product.domain.Product;
import net.sinzak.server.user.domain.User;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ChatRoomQueryService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;

    public JSONObject getChatRooms(User user){
        if(user ==null){
            return PropertyUtil.responseMessage(UserNotFoundException.USER_NOT_LOGIN);
        }
        List<GetChatRoomsDto> chatRoomsDtos = userChatRoomRepository
                .findUserChatRoomByEmail(user.getEmail()).stream()
                .map(
                        userChatRoom ->
                                GetChatRoomsDto.builder()
                                        .roomName(userChatRoom.getRoomName())
                                        .image(userChatRoom.getImage())
                                        .univ(userChatRoom.getOpponentUserUniv())
                                        .roomUuid(userChatRoom.getChatRoom().getRoomUuid())
                                        .build()
                )
                .collect(Collectors.toList());
        return PropertyUtil.response(chatRoomsDtos);
    }
    public Page<GetChatMessageDto> getChatRoomMessage(String roomUuid, Long messageId,Pageable pageable){
        ChatRoom findChatRoom = chatRoomRepository.findByRoomUuidFetchChatMessage(roomUuid)
                .orElseThrow(()->new InstanceNotFoundException("존재하지 않는 채팅방입니다."));
        List<GetChatMessageDto> getChatMessageDtos = findChatRoom.getChatMessages().stream().map(
                chatMessage -> GetChatMessageDto.builder()
                        .senderName(chatMessage.getSenderName())
                        .messageId(chatMessage.getId())
                        .sendAt(chatMessage.getCreatedDate())
                        .message(chatMessage.getMessage())
                        .build()
        ).collect(Collectors.toList());
        return new PageImpl<>(getChatMessageDtos,pageable,getChatMessageDtos.size());
    }

    public JSONObject getChatRoom(String roomUuid,User user){
        if(user ==null){
            return PropertyUtil.responseMessage(UserNotFoundException.USER_NOT_LOGIN);
        }
        List<UserChatRoom> userChatRooms = userChatRoomRepository
                .findUserChatRoomByEmail(user.getEmail());
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
        return PropertyUtil.responseMessage("찾는 채팅방이 없습니다.");
    }

    private GetChatRoomDto makeWorkChatRoomDto(UserChatRoom userChatRoom, ChatRoom chatRoom) {
        GetChatRoomDto getChatRoomDto = GetChatRoomDto.builder()
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
