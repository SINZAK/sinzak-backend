package net.sinzak.server.chatroom.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.chatroom.domain.ChatMessage;
import net.sinzak.server.chatroom.dto.request.ChatMessageDto;
import net.sinzak.server.chatroom.service.ChatMessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

//@Controller
//@RequiredArgsConstructor
//@Slf4j
//
//public class ChatMessageController {
////    private final SimpMessagingTemplate template;
//    private final ChatMessageService chatMessageService;
//
//    //    // /pub/chat/enter로 발행요청을 하면 /sub/chatroom/{roomId}로 메시지 전송
//    @MessageMapping(value = "/chat/enter") //메시지 매핑은 자동으로 앞에 /pub이 붙음(config)
//    public void enter(ChatMessageDto chatMessageDto) {
//        ChatMessage chatMessage = ChatMessage.builder()
//                .message(chatMessageDto.getSender()+"님이 채팅방에 참여하였습니다.")
//                .sender(chatMessageDto.getSender())
//                .roomId(chatMessageDto.getRoomId())
//                .type(chatMessageDto.getMessageType())
//                .build();
//        chatMessageDto.setMessage(chatMessageDto.getSender()+"님이 채팅방에 참여하였습니다.");
//        log.info("메시지 구독"+chatMessageDto.getRoomId());
//        template.convertAndSend("/sub/chat/rooms/"+chatMessage.getRoomId(),chatMessageDto);
//    }
//    @MessageMapping(value ="/chat/message")
//    public void message(ChatMessageDto chatMessageDto){
//        log.info("메시지 구독"+chatMessageDto.getRoomId());
//        template.convertAndSend("/sub/chat/rooms/"+chatMessageDto.getRoomId(),chatMessageDto);
//    }
//
//
////    @GetMapping("/getChatMessage")
////    public ChatMessage getChatMessage(@RequestParam String id) throws Exception{
////        return chatMessageService.getChatMessage(id);
////    }
////
////    @ApiOperation(value = "채팅 메시지 생성")
////    @PostMapping("/createChatMessage")
////    public JSONObject jsonObject(@RequestBody ChatMessageDto chatMessageDto) throws Exception{
////        return chatMessageService.createChatMessage(chatMessageDto);
////    }
//
//}
