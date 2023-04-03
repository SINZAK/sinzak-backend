package net.sinzak.server.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.UserUtils;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.common.error.UserNotLoginException;
import net.sinzak.server.common.resource.ApiDocumentResponse;
import net.sinzak.server.user.service.UserQueryService;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;

@Api(tags = "유저-조회")
@RestController
@RequiredArgsConstructor
public class UserQueryController {
    private final UserQueryService userQueryService;

    @ApiOperation(value ="내 프로필 보기")
    @GetMapping(value ="/users/my-profile")
    public JSONObject getMyProfile(){
        return userQueryService.getMyProfile();
    }

    @ApiOperation(value ="유저 프로필 보기")
    @GetMapping(value ="/users/{userId}/profile")
    public JSONObject getUserProfile(@PathVariable Long userId){
        try{
            Long currentUserId = UserUtils.getContextHolderId();
            return userQueryService.getUserProfileForUser(currentUserId, userId);
        }
        catch (UserNotFoundException | UserNotLoginException e){
            return userQueryService.getUserProfileForGuest(userId); /** 비회원용 **/
        }

    }

    @ApiOperation(value ="모든 유저 목록 보기")
    @GetMapping(value="/users")
    public JSONObject getAllUser(){
        return userQueryService.getAllUser();
    }

    @ApiOperation(value ="팔로워리스트")
    @GetMapping(value ="/users/{userId}/followers")
    public JSONObject getFollowerList(@PathVariable Long userId) {
        return userQueryService.getFollowerDtoList(userId);
    }

    @ApiOperation(value ="팔로잉리스트")
    @GetMapping(value ="/users/{userId}/followings")
    public JSONObject  getFollowingList(@PathVariable Long userId) {
        return userQueryService.getFollowingDtoList(userId);
    }

    @ApiOperation(value = "검색기록 출력", notes = "GetMapping에 유의 삭제는 Post로")
    @GetMapping(value = "/users/history")
    public JSONObject showHistory() {
        return userQueryService.showSearchHistory();
    }

    @ApiOperation(value ="찜 목록")
    @GetMapping(value ="/users/wish")
    public JSONObject showWish(){
        return userQueryService.getWishList();
    }
    @ApiOperation(value ="의뢰해요 목록")
    @GetMapping(value ="/users/work-employ")
    public JSONObject showWorkEmploy(){
        return userQueryService.getWorkEmploys();
    }

    @ApiDocumentResponse
    @ApiOperation(value = "신고 목록 출력")
    @PostMapping(value = "/users/reportlist")
    public JSONObject reportList(){
        return userQueryService.showReportList();
    }

    @ApiDocumentResponse
    @ApiOperation(value = "안드로이드 버전 출력", notes = "무시")
    @PostMapping(value = "/aos/version")
    public JSONObject version(){return PropertyUtil.response(12);}
}
