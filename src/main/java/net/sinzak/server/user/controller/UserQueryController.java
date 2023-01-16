package net.sinzak.server.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.dto.IdDto;
import net.sinzak.server.common.resource.ApiDocumentResponse;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.dto.request.UserIdDto;
import net.sinzak.server.user.dto.respond.GetFollowDto;
import net.sinzak.server.user.dto.respond.UserDto;
import net.sinzak.server.user.service.UserQueryService;
import org.json.simple.JSONObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Api(tags = "유저-조회")
@RestController
@RequiredArgsConstructor
public class UserQueryController {
    private final UserQueryService userQueryService;

    @ApiOperation(value ="내 프로필 보기")
    @GetMapping(value ="/users/my-profile")
    public UserDto getMyProfile(@AuthenticationPrincipal User user){
        return userQueryService.getMyProfile(user);
    }

    @ApiOperation(value ="유저 프로필 보기")
    @GetMapping(value ="/users/{userId}/profile")
    public UserDto getUserProfile(@PathVariable Long userId, @AuthenticationPrincipal User user){
        UserDto userDto = userQueryService.getUserProfile(userId, user);
        return userDto;
    }
    @ApiOperation(value ="팔로워리스트")
    @GetMapping(value ="/users/{userId}/followers")
    public List<GetFollowDto> getFollowerList( @PathVariable Long userId) {
        List<GetFollowDto> getFollowDtos =
               userQueryService.getFollowerDtoList(userId);
        return getFollowDtos;
    }

    @ApiOperation(value ="팔로잉리스트")
    @GetMapping(value ="/users/{userId}/followings")
    public List<GetFollowDto> getFollowingList(@PathVariable Long userId) {
        List<GetFollowDto> getFollowDtos =
                userQueryService.getFollowingDtoList(userId);
        return getFollowDtos;
    }

    @ApiOperation(value = "검색기록 출력", notes = "해당 기록의 id 포스트로 주시면 삭제합니다.")
    @GetMapping(value = "/users/history")
    public JSONObject showHistory(@RequestBody IdDto idDto, @AuthenticationPrincipal User user) {
        return userQueryService.deleteSearchHistory(idDto.getId(), user);
    }

}
