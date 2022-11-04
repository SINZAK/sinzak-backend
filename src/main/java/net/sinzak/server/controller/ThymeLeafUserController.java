package net.sinzak.server.controller;

import lombok.RequiredArgsConstructor;
import net.sinzak.server.domain.User;
import net.sinzak.server.repository.UserRepository;
import net.sinzak.server.service.UserCommandService;
import net.sinzak.server.service.UserQueryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ThymeLeafUserController {
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    private final UserRepository userRepository;
    @RequestMapping(value ="/home")
    public ModelAndView home(){
        ModelAndView modelAndView = new ModelAndView("home");
        List<User> users = userRepository.findAll();
        modelAndView.addObject("userList", users);
        return modelAndView;
    }

}
