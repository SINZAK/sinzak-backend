package net.sinzak.server.chatroom.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.chatroom.repository.ChatRoomRepository;
import net.sinzak.server.chatroom.service.ChatRoomQueryService;
import net.sinzak.server.user.domain.User;
import org.json.simple.JSONObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;


@Api(tags = "채팅-조회")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value ="/chat")
public class ChatRoomQueryController {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomQueryService chatRoomQueryService;

//    //아래는 테스트용
//    //채팅방 전체조회
//    @GetMapping(value = "/rooms")
//    public ModelAndView getRooms(){
//        log.info("#모든 채팅방 목록");
//        ModelAndView mv = new ModelAndView("chat/rooms");
//        mv.addObject("list",chatRoomRepository.findAll());
//        return mv;
//    }
//
//    //채팅방 조회
//    @GetMapping("/room")
//    public void getRoom(String roomId, Model model){
//        log.info("#채팅방 조회, 채팅방 아이디 :" +roomId );
//        model.addAttribute("room",chatRoomRepository.findByRoomId(roomId).get());
//    }

    @PostMapping("/rooms")
    @ApiOperation(value ="채팅방 목록 조회")
    public JSONObject getChatRooms(@AuthenticationPrincipal User user){
        return chatRoomQueryService.getChatRooms(user);
    }

    @PostMapping(value = "/rooms/{uuid}")
    @ApiOperation(value ="채팅방 정보 조회 ")
    public JSONObject getChatRoom(@PathVariable("uuid") String roomUuid, @AuthenticationPrincipal User user){
        return chatRoomQueryService.getChatRoom(roomUuid,user);
    }


}
