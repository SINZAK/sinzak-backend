package net.sinzak.server.chatroom.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.chatroom.repository.ChatRoomRepository;
import net.sinzak.server.chatroom.service.ChatRoomCommandService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ThymeleafChatRoomController {
    //    채팅방 개설
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomCommandService chatRoomCommandService;
    @PostMapping(value = "/chat/room")
    public String createRoom(@RequestParam String roomName, RedirectAttributes rttr){
        log.info("#채팅방 개설,채팅방 이름: "+ roomName);
        chatRoomCommandService.makeChatRoom(roomName);
        rttr.addFlashAttribute("roomName",roomName);
        return "redirect:/chat/rooms";
    }
        //아래는 테스트용
    //채팅방 전체조회
    @GetMapping(value = "/chat/rooms")
    public ModelAndView getRooms(){
        log.info("#모든 채팅방 목록");
        ModelAndView mv = new ModelAndView("chat/rooms");
        mv.addObject("list",chatRoomRepository.findAll());
        return mv;
    }
    @GetMapping("/chat/room")
    public void getRoom(String roomId, Model model){
        log.info("#채팅방 조회, 채팅방 아이디 :" +roomId );
        model.addAttribute("room",chatRoomRepository.findByRoomId(roomId).get());
    }

}
