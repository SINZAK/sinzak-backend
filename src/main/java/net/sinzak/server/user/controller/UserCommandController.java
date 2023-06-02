package net.sinzak.server.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import net.sinzak.server.common.UserUtils;
import net.sinzak.server.common.dto.IdDto;
import net.sinzak.server.common.resource.ApiDocumentResponse;
import net.sinzak.server.config.auth.SecurityService;
import net.sinzak.server.config.auth.jwt.TokenDto;
import net.sinzak.server.user.dto.request.*;
import net.sinzak.server.user.service.UserCommandService;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.security.NoSuchAlgorithmException;

@Api(tags = "유저-명령")
@RestController
@RequiredArgsConstructor
public class UserCommandController {
    private final UserCommandService userCommandService;
    private final SecurityService securityService;

    @ApiDocumentResponse
    @ApiOperation(value = "회원가입", notes = "카테고리는 {\"category_like\" : \"orient,painting\"} 처럼 콤마로만 구분해서 보내주세요\n 메일 인증 단계 이후에 한꺼번에 보내주세요 디스코드에 기재해놓겠습니다.")
    @PostMapping("/join")
    public JSONObject join(@Valid @RequestBody JoinDto dto) {
        return securityService.join(dto);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "닉네임 체크", notes = "이미 존재하는 닉네임일때 -> success : false, message : 이미 존재하는 닉네임입니다.")
    @PostMapping("/check/nickname")
    public JSONObject checkNickName(@RequestBody NickNameDto nickNameDto) {
        return userCommandService.checkNickName(nickNameDto.getNickName());
    }

    @ApiDocumentResponse
    @ApiOperation(value = "이메일 중복체크", notes = "이메일 중복 시 에는 하단과 같이 반환됩니다 false면 가입 불가능한 이메일인거로 생각하시면 됩니다 \n{\n" +
            "  \"success\": false,\n" +
            "  \"message\": \"이미 가입된 이메일입니다.\"\n" +
            "}", hidden = true)
    @PostMapping("/check/email")
    public JSONObject checkEmail(@RequestBody EmailDto dto) {
        return securityService.checkEmail(dto);
    }

    @ApiOperation(value = "로그인테스트 \"email\" : \"insi2000@naver.com\" 과 같은 형식으로 보내주세요", notes = "성공시 jwt 토큰을 헤더에 넣어서 반환합니다. Authorization 헤더에 액세스토큰을 넣어주세요")
    @PostMapping("/login")
    public TokenDto login(@RequestBody EmailDto dto) {
        return securityService.login(dto.getEmail());
    }

    @ApiOperation(value = "토큰 만료시 재발급 토큰들은 필요없고 헤더에 Authorization만 있으면 됩니다.")
    @PostMapping("/reissue")
    public TokenDto reissue() {
        return securityService.reissue();
    }

    @ApiDocumentResponse
    @ApiOperation(value = "유저 정보변경", notes = "이름, 한줄 소개")
    @PostMapping(value = "/users/edit")
    public JSONObject updateUser(@RequestBody UpdateUserDto dto) {
        return userCommandService.updateUser(dto);
    }

    @ApiDocumentResponse
    @ApiOperation(value ="프로필 이미지 변경")
    @PostMapping(value ="/users/edit/image")
    public JSONObject updateUserImage(@RequestPart MultipartFile multipartFile){
        return userCommandService.updateUserImage(multipartFile);
    }

    @ApiDocumentResponse
    @ApiOperation(value ="관심장르 업데이트")
    @PostMapping(value ="/users/edit/category")
    public JSONObject updateCategoryLike(@RequestBody CategoryDto categoryDto){
        return userCommandService.updateCategoryLike(categoryDto);
    }


    @ApiDocumentResponse
    @ApiOperation(value = "팔로우하기")
    @PostMapping(value = "/users/follow")
    public JSONObject followUser(@RequestBody UserIdDto userIdDto){
        return userCommandService.follow(UserUtils.getContextHolderId(), userIdDto.getUserId());
    }

    @ApiDocumentResponse
    @ApiOperation(value = "언팔로우하기")
    @PostMapping(value = "/users/unfollow")
    public JSONObject unFollowUser(@RequestBody UserIdDto userIdDto){
        return userCommandService.unFollow(UserUtils.getContextHolderId(), userIdDto.getUserId());
    }

    @ApiDocumentResponse
    @ApiOperation(value = "신고하기")
    @PostMapping(value = "/users/report")
    public JSONObject report(@RequestBody ReportRequestDto reportRequestDto){
        return userCommandService.report(reportRequestDto);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "신고 취소하기", notes = "userId만 제대로 주시면 응답합니다. (reason은 생략)")
    @PostMapping(value = "/users/report/cancel")
    public JSONObject reportCancel(@RequestBody ReportRequestDto reportRequestDto){
        return userCommandService.reportCancel(reportRequestDto);
    }

    @ApiDocumentResponse
    @ApiOperation(value ="fcm 토큰 저장", notes = "로그아웃할 땐 fcm토큰 빈칸")
    @PostMapping(value = "/users/fcm")
    public JSONObject setToken(FcmDto fcmDto){
        return userCommandService.setToken(fcmDto);
    }

    @ApiOperation(value = "검색기록 삭제", notes = "Post인 것에 유의하고 같은 url을 사용하려고 합니다. 해당 기록의 id를 주시면 삭제합니다.")
    @PostMapping(value = "/users/history")
    public JSONObject deleteHistory(@RequestBody IdDto idDto) {
        return userCommandService.deleteSearchHistory(idDto.getId());
    }

    @ApiOperation(value = "검색기록 전체! 삭제", notes = "전체 삭제입니다")
    @PostMapping(value = "/users/deletehistories")
    public JSONObject deleteHistory() {
        return userCommandService.deleteSearchHistory();
    }

    @ApiDocumentResponse
    @ApiOperation(value = "유저 탈퇴하기")
    @PostMapping(value = "/users/resign")
    public JSONObject resign(){
        return userCommandService.resign();
    }



}

