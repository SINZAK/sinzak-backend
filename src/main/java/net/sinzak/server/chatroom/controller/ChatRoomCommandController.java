package net.sinzak.server.chatroom.controller;


import io.swagger.annotations.Api;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Api(tags =" 채팅-명령")
@RestController
@RequiredArgsConstructor
@Slf4j
@ApiDocumentResponse
public class ChatRoomCommandController {
    private final ChatRoomCommandService chatRoomCommandService;


    @PostMapping (value ="/chat/rooms/create")
    @ApiOperation(value ="채팅방 생성",notes = "로그인한 유저, 글 아이디(postId),글 타입(postType = work,product ) 사용")
    public JSONObject createChatRoom(@RequestBody PostDto postDto, @AuthenticationPrincipal User user){
        return chatRoomCommandService.createUserChatRoom(postDto,user);
    }

    //채팅방 조회











}
