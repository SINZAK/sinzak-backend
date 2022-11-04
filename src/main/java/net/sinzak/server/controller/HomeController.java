package net.sinzak.server.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.domain.User;
import net.sinzak.server.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserRepository userRepository;

    @ApiOperation(value = "API 명세서")
    @RequestMapping("/api")
    public String api() { return "redirect:/swagger-ui.html";}

    @GetMapping("home")
    public String hello(Model model){
        return "home";
    }

    @GetMapping("/userList")
    public String userList(Model model){
        List<User> users = userRepository.findAll();
        model.addAttribute("users",users);
        return "userList";
    }

}
