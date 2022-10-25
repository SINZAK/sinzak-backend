package net.sinzak.server.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.domain.User;
import net.sinzak.server.service.UserQueryService;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class UserQueryController {
    private final UserQueryService userQueryService;

    @ApiOperation(value ="유저 프로필 보기")
    @GetMapping(value ="/users/{userId}")
    public JSONObject getUser(@PathVariable("userId") Long userId, User user){
        JSONObject jsonObject = userQueryService.getUserProfile(userId, user);
        return jsonObject;
    }

}
