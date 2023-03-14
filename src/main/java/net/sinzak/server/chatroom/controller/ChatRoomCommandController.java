package net.sinzak.server.chatroom.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.chatroom.dto.request.PostDto;
import net.sinzak.server.chatroom.service.ChatRoomCommandService;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.resource.ApiDocumentResponse;
import net.sinzak.server.user.domain.User;
import org.json.simple.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        PropertyUtil.checkHeader(user);
        return chatRoomCommandService.createUserChatRoom(postDto,user);
    }
    @PostMapping(value ="/chat/rooms/{uuid}/image",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public JSONObject uploadImage(@PathVariable("uuid") String uuid, @RequestPart List<MultipartFile> multipartFile){
        return chatRoomCommandService.uploadImage(uuid,multipartFile);
    }



//    @PostMapping(value ="/chat/rooms/{uuid}/leave")
//    @ApiOperation(value ="채팅방 나가기")
//    public JSONObject leaveChatRoom(@PathVariable("uuid") String roomUuid,@AuthenticationPrincipal User user){
//        return chatRoomCommandService.leaveChatRoom(user,roomUuid);
//    }

    //채팅방 조회











}
