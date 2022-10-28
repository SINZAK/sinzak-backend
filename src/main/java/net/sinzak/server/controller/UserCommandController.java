package net.sinzak.server.controller;


import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import net.sinzak.server.config.auth.LoginUser;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.dto.request.UpdateUserDto;
import net.sinzak.server.error.InstanceNotFoundException;
import net.sinzak.server.service.UserCommandService;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class UserCommandController {
    private final UserCommandService userCommandService;
    //로그인 연동이니 테스트용
    @ApiOperation(value = "유저생성")
    @PostMapping(value = "/users")
    public JSONObject createUser(@RequestBody SessionUser user) {
        JSONObject obj = new JSONObject();
        try {
            userCommandService.createUser(user);
            obj.put("success", true);
            return obj;
        } catch (InstanceNotFoundException e) {
            obj.put("success", false);
            return obj;
        }
    }
    @ApiOperation(value = "유저 정보변경", notes = "이름,한줄 소개, 학교(보류) ")
    @PutMapping(value = "/users")
    public JSONObject updateUser(@RequestBody UpdateUserDto dto ,@LoginUser SessionUser user) {
        return userCommandService.updateUser(dto,user);
    }
}

