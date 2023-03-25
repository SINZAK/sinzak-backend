package net.sinzak.server.chatroom.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.chatroom.dto.request.PostDto;
import net.sinzak.server.chatroom.dto.respond.GetChatMessageDto;
import net.sinzak.server.chatroom.service.ChatRoomQueryService;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;


@Api(tags = "채팅-조회")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value ="/chat")
public class ChatRoomQueryController {
    private final ChatRoomQueryService chatRoomQueryService;
    private static final int MESSAGE_PAGE_SIZE =30;


    @PostMapping("/rooms")
    @ApiOperation(value ="채팅방 목록 조회")
    public JSONObject getChatRooms(){
        return chatRoomQueryService.getChatRooms();
    }

    @PostMapping(value = "/rooms/{uuid}")
    @ApiOperation(value ="채팅방 정보 조회 " ,notes = "postUserId = 게시글 올린 유저아이디, opponentUserId = 채팅 하는 상대방 아이디 ,삭제된 게시글일시 productId : null ->삭제된 게시글")
    public JSONObject getChatRoom(@PathVariable("uuid") String roomUuid){
        return chatRoomQueryService.getChatRoom(roomUuid);
    }

    @PostMapping(value ="/rooms/post")
    @ApiOperation(value = "상품에 딸려있는 채팅방 불러오기")
    public JSONObject getChatRoomByProduct(@RequestBody PostDto postDto){
        return chatRoomQueryService.getChatRoomsByPost(postDto);
    }

    @GetMapping(value = "/rooms/{uuid}/message")
    public Page<GetChatMessageDto> getChatRoomMessage(
            @PathVariable("uuid") String roomUuid, @RequestParam(value = "page",required = false,defaultValue = "0") int page){
        PageRequest pageRequest = PageRequest.of(page,MESSAGE_PAGE_SIZE,Sort.by("messageId").descending());
        return chatRoomQueryService.getChatRoomMessage(roomUuid,pageRequest);
    }

}
