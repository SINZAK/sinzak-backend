package net.sinzak.server.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.dto.IdDto;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.common.resource.ApiDocumentResponse;
import net.sinzak.server.config.auth.SecurityService;
import net.sinzak.server.config.auth.jwt.TokenDto;
import net.sinzak.server.config.auth.jwt.TokenRequestDto;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.dto.request.*;
import net.sinzak.server.user.service.UserCommandService;
import org.json.simple.JSONObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "유저-명령")
@RestController
@RequiredArgsConstructor
public class UserCommandController {
    private final UserCommandService userCommandService;
    private final SecurityService securityService;

    @ApiDocumentResponse
    @ApiOperation(value = "회원가입", notes = "카테고리는 {\"category_like\" : \"orient,painting\"} 처럼 콤마로만 구분해서 보내주세요\n 메일 인증 단계 이후에 한꺼번에 보내주세요 디스코드에 기재해놓겠습니다.")
    @PostMapping("/join")
    public JSONObject join(@AuthenticationPrincipal User user, @RequestBody JoinDto dto) {
        PropertyUtil.checkHeader(user);
        return securityService.join(user, dto);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "회원가입", notes = "이미 존재하는 닉네임일때 -> success : false, message : 이미 존재하는 닉네임입니다.")
    @PostMapping("/check/nickname")
    public JSONObject checkNickName(@RequestBody NickNameDto nickNameDto) {
        return userCommandService.checkNickName(nickNameDto.getNickName());
    }

    @ApiDocumentResponse
    @ApiOperation(value = "이메일 중복체크", notes = "이메일 중복 시 에는 하단과 같이 반환됩니다 false면 가입 불가능한 이메일인거로 생각하시면 됩니다 \n{\n" +
            "  \"success\": false,\n" +
            "  \"message\": \"이미 가입된 이메일입니다.\"\n" +
            "}")
    @PostMapping("/check/email")
    public JSONObject checkEmail(@RequestBody EmailDto dto) {
        return securityService.checkEmail(dto);
    }

    @ApiOperation(value = "로그인테스트 \"email\" : \"insi2000@naver.com\" 과 같은 형식으로 보내주세요", notes = "성공시 jwt 토큰을 헤더에 넣어서 반환합니다. Authorization 헤더에 액세스토큰을 넣어주세요")
    @PostMapping("/login")
    public TokenDto login(@RequestBody EmailDto dto) {
        return securityService.login(dto.getEmail());
    }

    @ApiOperation(value = "토큰 만료시 재발급, access,refresh 둘 다 보내주세요")
    @PostMapping("/reissue")
    public TokenDto reissue(@AuthenticationPrincipal User user, @RequestBody TokenRequestDto tokenRequestDto) {
        PropertyUtil.checkHeader(user);
        return securityService.reissue(user, tokenRequestDto);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "유저 정보변경", notes = "이름,한줄 소개, 학교(보류) ")
    @PostMapping(value = "/users/edit")
    public JSONObject updateUser( @RequestBody UpdateUserDto dto , @AuthenticationPrincipal User user) {
        PropertyUtil.checkHeader(user);
        return userCommandService.updateUser(dto,user);
    }
    @ApiDocumentResponse
    @ApiOperation(value ="프로필 이미지 변경")
    @PostMapping(value ="/users/edit/image")
    public JSONObject updateUserImage(@AuthenticationPrincipal User user,@RequestPart MultipartFile multipartFile){
        PropertyUtil.checkHeader(user);
        return userCommandService.updateUserImage(user,multipartFile);
    }

    @ApiDocumentResponse
    @ApiOperation(value ="관심장르 업데이트")
    @PostMapping(value ="/users/edit/category")
    public JSONObject updateCategoryLike(@AuthenticationPrincipal User user,@RequestBody CategoryDto categoryDto){
        PropertyUtil.checkHeader(user);
        return userCommandService.updateCategoryLike(user,categoryDto);
    }


    @ApiDocumentResponse
    @ApiOperation(value = "팔로우하기")
    @PostMapping(value = "/users/follow")
    public JSONObject followUser(@RequestBody UserIdDto userIdDto, @AuthenticationPrincipal User user){
        PropertyUtil.checkHeader(user);
        return userCommandService.follow(userIdDto.getUserId(),user);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "언팔로우하기")
    @PostMapping(value = "/users/unfollow")
    public JSONObject unFollowUser(@RequestBody UserIdDto userIdDto, @AuthenticationPrincipal User user){
        PropertyUtil.checkHeader(user);
        return userCommandService.unFollow(userIdDto.getUserId(),user);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "신고하기")
    @PostMapping(value = "/users/report")
    public JSONObject report(@RequestBody ReportDto reportDto, @AuthenticationPrincipal User user){
        PropertyUtil.checkHeader(user);
        return userCommandService.report(reportDto, user);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "신고 취소하기", notes = "userId만 제대로 주시면 응답합니다. (reason은 생략)")
    @PostMapping(value = "/users/report/cancel")
    public JSONObject reportCancel(@RequestBody ReportDto reportDto, @AuthenticationPrincipal User user){
        PropertyUtil.checkHeader(user);
        return userCommandService.reportCancel(reportDto, user);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "신고 목록 출력")
    @PostMapping(value = "/users/reportlist")
    public JSONObject reportList(@AuthenticationPrincipal User user){
        PropertyUtil.checkHeader(user);
        return userCommandService.showReportList(user);
    }
    @ApiDocumentResponse
    @ApiOperation(value ="fcm 토큰 저장")
    @PostMapping(value = "/users/fcm")
    public JSONObject setToken(FcmDto fcmDto){
        return userCommandService.setToken(fcmDto);
    }

    @ApiOperation(value = "검색기록 삭제", notes = "Post인 것에 유의하고 같은 url을 사용하려고 합니다. 해당 기록의 id를 주시면 삭제합니다.")
    @PostMapping(value = "/users/history")
    public JSONObject deleteHistory(@RequestBody IdDto idDto, @AuthenticationPrincipal User user) {
        PropertyUtil.checkHeader(user);
        return userCommandService.deleteSearchHistory(idDto.getId(), user);
    }

    @ApiOperation(value = "검색기록 전체! 삭제", notes = "전체 삭제입니다")
    @PostMapping(value = "/users/deletehistories")
    public JSONObject deleteHistory(@AuthenticationPrincipal User user) {
        PropertyUtil.checkHeader(user);
        return userCommandService.deleteSearchHistory(user);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "유저 탈퇴하기")
    @PostMapping(value = "/users/resign")
    public JSONObject report(@AuthenticationPrincipal User user){
        PropertyUtil.checkHeader(user);
        return userCommandService.resign(user);
    }

}

