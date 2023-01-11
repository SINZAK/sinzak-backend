package net.sinzak.server.common.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.config.auth.LoginUser;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.product.dto.ProductPostDto;
import net.sinzak.server.user.dto.request.UserIdDto;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Api(hidden = true)
@Controller
public class ApiController {

    @GetMapping("/api")
    public String home(){
        return "redirect:/swagger-ui.html";
    }
}
