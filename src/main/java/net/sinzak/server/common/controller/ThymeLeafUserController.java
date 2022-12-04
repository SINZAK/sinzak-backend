package net.sinzak.server.common.controller;

import lombok.RequiredArgsConstructor;
import net.sinzak.server.config.auth.LoginUser;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.product.dto.ProductPostDto;
import net.sinzak.server.work.dto.WorkPostDto;
import net.sinzak.server.user.dto.respond.GetFollowDto;
import net.sinzak.server.product.repository.ProductRepository;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.repository.UserRepository;
import net.sinzak.server.work.repository.WorkRepository;
import net.sinzak.server.user.service.UserCommandService;
import net.sinzak.server.user.service.UserQueryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ThymeLeafUserController {
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    private final UserRepository userRepository;
    private final WorkRepository workRepository;
    private final ProductRepository productRepository;
    @RequestMapping("/home")
    public ModelAndView home(@LoginUser SessionUser user){
        ModelAndView modelAndView = new ModelAndView("home");
        User User = userRepository.findByEmail(user.getEmail()).get();
        modelAndView.addObject("userId", User.getId());
        return modelAndView;
    }
    @RequestMapping("/users/{userId}/thymeleaf")
    public ModelAndView userProfile(@LoginUser SessionUser loginUser, @PathVariable("userId") Long userId){
        ModelAndView modelAndView = new ModelAndView("profile");
        Optional<User> findUser= userRepository.findById(userId);
        if(findUser.isPresent()){
            User User = findUser.get();
            modelAndView.addObject("userName",User.getName());
            modelAndView.addObject("followerNum",User.getFollowerList().size());
            modelAndView.addObject("followingNum",User.getFollowingList().size());
            modelAndView.addObject("imageUrl",User.getPicture());
            modelAndView.addObject("userId",userId);
        }
        else{
            modelAndView.addObject("userName","호날두");
            modelAndView.addObject("followerNum","999");
            modelAndView.addObject("followingNum","999");
            modelAndView.addObject("imageUrl","https://img.khan.co.kr/news/2022/07/27/l_2022072801001731200145941.webp");
        }
        return modelAndView;
    }
    @GetMapping("/users/{userId}/followers/thymeleaf")
    public ModelAndView getFollowerList(@PathVariable("userId") Long userId){
        List<GetFollowDto> getFollowDtoList = userQueryService.getFollowerDtoList(userId);
        ModelAndView modelAndView = new ModelAndView("followList");
        modelAndView.addObject("followerNum",getFollowDtoList.size());
        modelAndView.addObject("followerList",getFollowDtoList);
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
