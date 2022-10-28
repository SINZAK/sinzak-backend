package net.sinzak.server.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.config.auth.LoginUser;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.dto.WorkPostDto;
import net.sinzak.server.service.WorkService;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;

@RestController
@RequiredArgsConstructor
public class WorkController {

    private final WorkService workService;

//    @ApiOperation(value = "외주 모집 글 생성")
//    @PostMapping("/works/build")
//    public JSONObject makeWorkPost(@LoginUser SessionUser user, @RequestBody WorkPostDto postDto) {
//        System.out.println(user.getName());
//        return workService.makeWork(user, postDto);
//    }

    @ApiOperation(value = "외주 모집 글 생성")
    @PostMapping("/works/build2")
    public JSONObject makeWorkPost(@LoginUser SessionUser user, @RequestBody WorkPostDto postDto) {
        return workService.makeWorkPost(user, postDto);
    }
}
