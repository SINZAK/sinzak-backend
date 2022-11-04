package net.sinzak.server.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.domain.Product;
import net.sinzak.server.domain.User;
import net.sinzak.server.domain.Work;
import net.sinzak.server.repository.ProductRepository;
import net.sinzak.server.repository.UserRepository;
import net.sinzak.server.repository.WorkRepository;
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
    private final ProductRepository productRepository;
    private final WorkRepository workRepository;

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

    @GetMapping("/productList")
    public String productList(Model model){
        List<Product> products = productRepository.findAll();
        model.addAttribute("products",products);
        return "productList";
    }

    @GetMapping("/workList")
    public String workList(Model model){
        List<Work> works = workRepository.findAll();
        model.addAttribute("works",works);
        return "workList";
    }

}
