package net.sinzak.server.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
public class HomeController {

    @ApiOperation(value = "API 명세서")
    @RequestMapping("/api")
    public String api() { return "redirect:/swagger-ui.html";}
}
