package net.sinzak.server.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class HomeController {


    @ApiOperation(value = "API 명세서")
    @RequestMapping("/api")
    public String api() { return "redirect:/swagger-ui.html";}

    @GetMapping("home")
    public String hello(Model model){
        return "home";
    }

}
