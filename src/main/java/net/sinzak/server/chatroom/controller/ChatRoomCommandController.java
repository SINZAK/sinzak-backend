package net.sinzak.server.chatroom.controller;


import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.chatroom.dto.request.PostDto;
import net.sinzak.server.chatroom.repository.ChatRoomRepository;
import net.sinzak.server.chatroom.service.ChatRoomCommandService;
import net.sinzak.server.common.resource.ApiDocumentResponse;
import net.sinzak.server.user.domain.User;
import org.json.simple.JSONObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequiredArgsConstructor
@Slf4j
@ApiDocumentResponse
public class ChatRoomCommandController {
    private final ChatRoomCommandService chatRoomCommandService;



    @PostMapping (value ="/chatRooms/create")
    @ApiOperation(value ="채팅방 생성",notes = "로그인한 유저, 글 아이디,글 타입(work or product) 사용")
    public JSONObject createChatRoom(@RequestBody PostDto postDto, @AuthenticationPrincipal User user){
        return chatRoomCommandService.createUserChatRoom(postDto,user);
    }
    //채팅방 개설
    @PostMapping(value = "/chat/room")
    public String createRoom(@RequestParam String roomName, RedirectAttributes rttr){
        log.info("#채팅방 개설,채팅방 이름: "+ roomName);
        chatRoomCommandService.makeChatRoom(roomName);
        rttr.addFlashAttribute("roomName",roomName);
        return "redirect:/chat/rooms";
    }









}
