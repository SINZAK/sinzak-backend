package net.sinzak.server.chatroom.controller;


import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.chatroom.domain.ChatMessage;
import net.sinzak.server.chatroom.dto.ChatMessageDto;
import net.sinzak.server.chatroom.service.ChatMessageService;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/getChatMessage")
    public ChatMessage getChatMessage(@RequestParam String id) throws Exception{
        return chatMessageService.getChatMessage(id);
    }

    @ApiOperation(value = "채팅 메시지 생성")
    @PostMapping("/createChatMessage")
    public JSONObject jsonObject(@RequestBody ChatMessageDto chatMessageDto) throws Exception{
        return chatMessageService.createChatMessage(chatMessageDto);
    }

}
