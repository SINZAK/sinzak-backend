package net.sinzak.server.controller;

import lombok.RequiredArgsConstructor;
import net.sinzak.server.config.auth.LoginUser;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.dto.ProductPostDto;
import net.sinzak.server.dto.WorkPostDto;
import net.sinzak.server.repository.ProductRepository;
import net.sinzak.server.repository.UserRepository;
import net.sinzak.server.repository.WorkRepository;
import net.sinzak.server.service.UserCommandService;
import net.sinzak.server.service.UserQueryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class ThymeLeafUserController {
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    private final UserRepository userRepository;
    private final WorkRepository workRepository;
    private final ProductRepository productRepository;
    @RequestMapping(value ="/home")
    public ModelAndView home(){
        ModelAndView modelAndView = new ModelAndView("home");
        modelAndView.addObject("userList", userRepository.findAll());
        return modelAndView;
    }

    @GetMapping("/userList")
    public String showUserList(Model model){
        model.addAttribute("users",userRepository.findAll());
        return "userList";
    }

    @GetMapping("/productList")
    public String showProductList(Model model){
        model.addAttribute("products",productRepository.findAll());
        return "productList";
    }

    @GetMapping("/workList")
    public String showWorkList(Model model){
        model.addAttribute("works",workRepository.findAll());
        return "workList";
    }

    @GetMapping("/products/build")
    public String buildProducts(Model model){
        model.addAttribute("product", new ProductPostDto());
        return "productForm";
    }

    @GetMapping("/works/build")
    public String buildWorks(Model model){
        model.addAttribute("work", new WorkPostDto());
        return "workForm";
    }

}
