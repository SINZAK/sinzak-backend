package net.sinzak.server.common.controller;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Api(hidden = true)
@Controller
public class ApiController {

    @GetMapping("/api")
    public String home(){
        return "redirect:/swagger-ui.html";
    }
}
