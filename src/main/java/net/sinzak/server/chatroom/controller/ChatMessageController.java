package net.sinzak.server.chatroom.controller;


import lombok.RequiredArgsConstructor;
import net.sinzak.server.chatroom.domain.ChatMessage;
import net.sinzak.server.chatroom.service.ChatMessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/getChatMessage")
    public ChatMessage getChatMessage(@RequestParam String id) throws Exception{
        return chatMessageService.getChatMessage(id);
    }

}
