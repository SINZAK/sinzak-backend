package net.sinzak.server.user.controller;


import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import net.sinzak.server.config.auth.SecurityService;
import net.sinzak.server.config.auth.jwt.TokenDto;
import net.sinzak.server.config.auth.jwt.TokenRequestDto;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.dto.request.JoinDto;
import net.sinzak.server.user.dto.request.UpdateUserDto;
import net.sinzak.server.user.service.UserCommandService;
import org.json.simple.JSONObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserCommandController {
    private final UserCommandService userCommandService;
    private final SecurityService securityService;


    @ApiOperation(value = "회원가입", notes = "{\"category_like\" : \"orient, western\" 처럼 콤마로 구분해서 보내주세요")
    @PostMapping("/join")
    public TokenDto join(@RequestBody JoinDto dto) {
        return securityService.join(dto);
    }

    @ApiOperation(value = "로그인테스트 \"email\" : \"insi2000@naver.com\" 과 같은 형식으로 보내주세요", notes = "성공시 jwt 토큰을 헤더에 넣어서 반환합니다. Authorization 헤더에 액세스토큰을 넣어주세요")
    @PostMapping("/login")
    public TokenDto login(@RequestBody Map<String, String> user) {
        return securityService.login(user);
    }

    @ApiOperation(value = "토큰 만료시 재발급, access,refresh 둘 다 보내주세요")
    @PostMapping("/reissue")
    public TokenDto reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        return securityService.reissue(tokenRequestDto);
    }

    @ApiOperation(value = "유저 정보변경", notes = "이름,한줄 소개, 학교(보류) ")
    @PostMapping(value = "/users/edit")
    public JSONObject updateUser( @RequestBody UpdateUserDto dto , @ApiIgnore @AuthenticationPrincipal User user) {
        return userCommandService.updateUser(dto,user);
    }
    @ApiOperation(value = "팔로우하기")
    @PostMapping(value = "/users/{userId}/follow")
    public JSONObject followUser(@PathVariable("userId") Long userId,@ApiIgnore @AuthenticationPrincipal User user){
        return userCommandService.follow(userId,user);
    }
    @ApiOperation(value = "언팔로우하기")
    @PostMapping(value = "/users/{userId}/unfollow")
    public JSONObject unFollowUser(@PathVariable("userId") Long userId,@ApiIgnore @AuthenticationPrincipal User user){
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

