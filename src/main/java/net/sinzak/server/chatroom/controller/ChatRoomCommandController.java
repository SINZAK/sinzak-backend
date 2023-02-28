package net.sinzak.server.chatroom.controller;


import com.google.api.client.json.Json;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.chatroom.dto.request.PostDto;
import net.sinzak.server.chatroom.repository.ChatRoomRepository;
import net.sinzak.server.chatroom.service.ChatRoomCommandService;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.error.ChatRoomNotFoundException;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.common.resource.ApiDocumentResponse;
import net.sinzak.server.user.domain.User;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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

    @PostMapping(value ="/chat/rooms/{uuid}/image")
    public JSONObject uploadImage(@PathVariable("uuid") String uuid, List<MultipartFile> files){
        return chatRoomCommandService.uploadImage(uuid,files);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    protected JSONObject handleUserNotFoundException() {
        return PropertyUtil.responseMessage(UserNotFoundException.USER_NOT_FOUND);
    }
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    protected JSONObject handleUserNotFoundException(UserNotFoundException e) {
        return PropertyUtil.responseMessage(e.getMessage());
    }
    @ExceptionHandler(ChatRoomNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    public JSONObject handleChatRoomNotFoundException(){
        return PropertyUtil.responseMessage("존재하지 않은 채팅방입니다");
    }

//    @PostMapping(value ="/chat/rooms/{uuid}/leave")
//    @ApiOperation(value ="채팅방 나가기")
//    public JSONObject leaveChatRoom(@PathVariable("uuid") String roomUuid,@AuthenticationPrincipal User user){
//        return chatRoomCommandService.leaveChatRoom(user,roomUuid);
//    }

    //채팅방 조회











}
