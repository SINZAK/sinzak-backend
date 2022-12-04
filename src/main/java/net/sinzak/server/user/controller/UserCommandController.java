package net.sinzak.server.user.controller;


import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import net.sinzak.server.config.auth.LoginUser;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.user.dto.request.UpdateUserDto;
import net.sinzak.server.common.error.InstanceNotFoundException;
import net.sinzak.server.user.service.UserCommandService;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor
public class UserCommandController {
    private final UserCommandService userCommandService;

    @ApiOperation(value = "유저 정보변경", notes = "이름,한줄 소개, 학교(보류) ")
    @PostMapping(value = "/users/edit")
    public JSONObject updateUser( @RequestBody UpdateUserDto dto , @ApiIgnore @LoginUser SessionUser user) {
        return userCommandService.updateUser(dto,user);
    }
    @ApiOperation(value = "팔로우하기")
    @PostMapping(value = "/users/{userId}/follow")
    public JSONObject followUser(@PathVariable("userId") Long userId,@ApiIgnore @LoginUser SessionUser user){
        return userCommandService.follow(userId,user);
    }
    @ApiOperation(value = "언팔로우하기")
    @PostMapping(value = "/users/{userId}/unfollow")
    public JSONObject unFollowUser(@PathVariable("userId") Long userId,@ApiIgnore @LoginUser SessionUser user){
        return userCommandService.unFollow(userId,user);
    }





    //로그인 연동이니 테스트용
//    @ApiOperation(value = "유저생성")
//    @PostMapping(value = "/users")
//    public JSONObject createUser( @RequestBody SessionUser user) {
//        JSONObject obj = new JSONObject();
//        try {
//            userCommandService.createUser(user);
//            obj.put("success", true);
//            return obj;
//        } catch (InstanceNotFoundException e) {
//            obj.put("success", false);
//            return obj;
//        }
//    }

//    @ApiOperation(value = "유저생성")
//    @PostMapping(value = "/users")
//    public JSONObject createUser2(@RequestBody SessionUser user) {
//        return userCommandService.createUser2(user);
//    }
}

