package net.sinzak.server.user.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.error.ErrorResponse;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.common.resource.ApiDocumentResponse;
import net.sinzak.server.config.auth.SecurityService;
import net.sinzak.server.config.auth.jwt.TokenDto;
import net.sinzak.server.config.auth.jwt.TokenRequestDto;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.dto.request.EmailDto;
import net.sinzak.server.user.dto.request.JoinDto;
import net.sinzak.server.user.dto.request.UnivDto;
import net.sinzak.server.user.dto.request.UpdateUserDto;
import net.sinzak.server.user.service.UserCommandService;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.util.NoSuchElementException;

@Api(tags = "유저-명령")
@RestController
@RequiredArgsConstructor
public class UserCommandController {
    private final UserCommandService userCommandService;
    private final SecurityService securityService;


    @ApiDocumentResponse
    @ApiOperation(value = "회원가입", notes = "카테고리는 {\"category_like\" : \"orient, painting\"} 처럼 콤마로 구분해서 보내주세요\n 1)로그인 성공 시 하단과 같이 반환됩니다\n" +
            "{\n" +
            "        \"success\": true,\n" +
            "            \"token\": {\n" +
            "        \"grantType\": \"bearer\",\n" +
            "                \"accessToken\": \"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpbnNlbzUxMkBuYXZlci5jb20iLCJyb2xlcyI6WyJST0xFX1VTRVIiXSwiaWF0IjoxNjcyNzE3ODQ1LCJleHAiOjE2NzI3MTk2NDV9.iRfb5SkbOiGuBBsGuXmP-R9WeH4T-npQM1I7ROZ9DVk\",\n" +
            "                \"refreshToken\": \"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NzM5Mjc0NDV9.WOaxJZI1292s5IcNHwhCazmT9izRC1TFfc3NBaCDhIg\",\n" +
            "                \"accessTokenExpireDate\": 1800000\n" +
            "    }\n" +
            "    }" +
            "\n\n\n\n 2) 이메일 중복 시 에는 하단과 같이 반환됩니다 :\n{\n" +
            "  \"success\": false,\n" +
            "  \"message\": \"이미 가입된 이메일입니다.\"\n" +
            "}")

    @PostMapping("/join")
    public JSONObject join(@RequestBody JoinDto dto) {
        return securityService.join(dto);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "이메일 중복체크", notes = "이메일 중복 시 에는 하단과 같이 반환됩니다 false면 가입 불가능한 이메일인거로 생각하시면 됩니다 \n{\n" +
            "  \"success\": false,\n" +
            "  \"message\": \"이미 가입된 이메일입니다.\"\n" +
            "}")
    @PostMapping("/checkemail")
    public JSONObject checkEmail(@RequestBody EmailDto dto) {
        return securityService.checkEmail(dto);
    }

    @ApiOperation(value = "로그인테스트 \"email\" : \"insi2000@naver.com\" 과 같은 형식으로 보내주세요", notes = "성공시 jwt 토큰을 헤더에 넣어서 반환합니다. Authorization 헤더에 액세스토큰을 넣어주세요")
    @PostMapping("/login")
    public TokenDto login(@RequestBody EmailDto dto) {
        return securityService.login(dto);
    }

    @ApiOperation(value = "토큰 만료시 재발급, access,refresh 둘 다 보내주세요")
    @PostMapping("/reissue")
    public TokenDto reissue(@AuthenticationPrincipal User user, @RequestBody TokenRequestDto tokenRequestDto) {
        return securityService.reissue(user, tokenRequestDto);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "유저 정보변경", notes = "이름,한줄 소개, 학교(보류) ")
    @PostMapping(value = "/users/edit")
    public JSONObject updateUser( @RequestBody UpdateUserDto dto , @ApiIgnore @AuthenticationPrincipal User user) {
        return userCommandService.updateUser(dto,user);
    }
    @ApiDocumentResponse
    @ApiOperation(value = "팔로우하기")
    @PostMapping(value = "/users/{userId}/follow")
    public JSONObject followUser(@PathVariable("userId") Long userId,@ApiIgnore @AuthenticationPrincipal User user){
        return userCommandService.follow(userId,user);
    }

    @ApiDocumentResponse
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
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected JSONObject handleException1() {
        return PropertyUtil.responseMessage("유효하지 않은 토큰입니다.");
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected JSONObject handleUserNotFoundException() {
        return PropertyUtil.responseMessage("가입되지 않은 ID입니다.");
    }
}

