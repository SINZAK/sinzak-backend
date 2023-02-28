package net.sinzak.server.chatroom.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.chatroom.domain.ChatMessage;
import net.sinzak.server.chatroom.dto.request.ChatMessageDto;
import net.sinzak.server.chatroom.dto.request.ChatRoomUuidDto;
import net.sinzak.server.chatroom.service.ChatMessageService;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.error.ChatRoomNotFoundException;
import net.sinzak.server.user.domain.User;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequiredArgsConstructor
@Slf4j

public class ChatMessageController {
    private final SimpMessagingTemplate template;
    private final ChatMessageService chatMessageService;

    //    // /pub/chat/enter로 발행요청을 하면 /sub/chatroom/{roomId}로 메시지 전송
    @MessageMapping(value = "/chat/enter") //메시지 매핑은 자동으로 앞에 /pub이 붙음(config)
    public void enter(ChatMessageDto chatMessageDto) {
        ChatMessage chatMessage = ChatMessage.builder()
                .message(chatMessageDto.getSenderName()+"님이 채팅방에 참여하였습니다.")
                .senderName(chatMessageDto.getSenderName())
                .roomId(chatMessageDto.getRoomId())
                .type(chatMessageDto.getMessageType())
                .build();
        chatMessageDto.setMessage(chatMessageDto.getSenderName()+"님이 채팅방에 참여하였습니다.");
        log.info("메시지 구독"+chatMessageDto.getRoomId());
        template.convertAndSend("/sub/chat/rooms/"+chatMessage.getRoomId(),chatMessageDto);
    }
    @MessageMapping(value ="/chat/message")
    public void message(ChatMessageDto chatMessageDto){
        log.info("메시지 구독"+chatMessageDto.getRoomId());
        chatMessageService.sendChatMessage(chatMessageDto);
    }

    @MessageMapping(value = "/chat/leave")
    public void leave(@AuthenticationPrincipal User user, ChatRoomUuidDto chatRoomUuidDto){
        chatMessageService.leaveChatRoom(user,chatRoomUuidDto);
    }
    @ExceptionHandler(ChatRoomNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    public JSONObject handleChatRoomNotFoundException(){
        return PropertyUtil.responseMessage("존재하지 않은 채팅방입니다");
    }

//    @GetMapping("/getChatMessage")
//    public ChatMessage getChatMessage(@RequestParam String id) throws Exception{
//        return chatMessageService.getChatMessage(id);
//    }
//
//    @ApiOperation(value = "채팅 메시지 생성")
//    @PostMapping("/createChatMessage")
//    public JSONObject jsonObject(@RequestBody ChatMessageDto chatMessageDto) throws Exception{
//        return chatMessageService.createChatMessage(chatMessageDto);
//    }

}
