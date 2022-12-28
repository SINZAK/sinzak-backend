package net.sinzak.server.user.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.config.auth.LoginUser;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.user.dto.respond.GetFollowDto;
import net.sinzak.server.user.service.UserQueryService;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserQueryController {
    private final UserQueryService userQueryService;

    @ApiOperation(value ="유저 프로필 보기")
    @GetMapping(value ="/users/{userId}/")
    public JSONObject getUser( @PathVariable("userId") Long userId,@ApiIgnore @LoginUser SessionUser user){
        JSONObject jsonObject = userQueryService.getUserProfile(userId, user);
        return jsonObject;
    }
    @ApiOperation(value ="팔로워리스트")
    @GetMapping(value ="/users/{userId}/followers")
    public List<GetFollowDto> getFollowerList( @PathVariable("userId") Long userId) {
        List<GetFollowDto> getFollowDtos =
               userQueryService.getFollowerDtoList(userId);
        return getFollowDtos;
    }

    @ApiOperation(value ="팔로잉리스트")
    @GetMapping(value ="/users/{userId}/followings")
    public List<GetFollowDto> getFollowingList(@PathVariable("userId") Long userId) {
        List<GetFollowDto> getFollowDtos =
                userQueryService.getFollowingDtoList(userId);
        return getFollowDtos;
    }


    //    @ApiOperation(value ="팔로워리스트")
//    @GetMapping(value ="/users/{userId}/followers")
//    public List<GetFollowDto> getFollowerList2(@PathVariable("userId") Long userId) {
//        return userQueryService.getFollowerDtoList(userId);
//    }



}
