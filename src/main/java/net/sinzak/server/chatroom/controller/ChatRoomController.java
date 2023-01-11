package net.sinzak.server.chatroom.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.chatroom.domain.ChatMessage;
import net.sinzak.server.chatroom.domain.ChatRoom;
import net.sinzak.server.chatroom.dto.ChatMessageDto;
import net.sinzak.server.chatroom.repository.ChatRoomRepository;
import net.sinzak.server.chatroom.service.ChatRoomQueryService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value ="/chat")
public class ChatRoomController {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomQueryService chatRoomQueryService;
    private final SimpMessagingTemplate template;


    //아래는 테스트용
    //채팅방 전체조회
    @GetMapping(value = "/rooms")
    public ModelAndView getRooms(){
        log.info("#모든 채팅방 목록");
        ModelAndView mv = new ModelAndView("chat/rooms");
        mv.addObject("list",chatRoomRepository.findAll());
        return mv;
    }
    //채팅방 개설
    @PostMapping(value = "/room")
    public String createRoom(@RequestParam String roomName, RedirectAttributes rttr){
        log.info("#채팅방 개설,채팅방 이름: "+ roomName);
        ChatRoom chatRoom = new ChatRoom(roomName);
        chatRoomRepository.save(chatRoom);
        rttr.addFlashAttribute("roomName",chatRoom.getName());
        return "redirect:/chat/rooms";
    }

    //채팅방 조회
    @GetMapping("/room")
    public void getRoom(String roomId, Model model){
        log.info("#채팅방 조회, 채팅방 아이디 :" +roomId );
        model.addAttribute("room",chatRoomRepository.findByRoomId(roomId).get());
    }








}
