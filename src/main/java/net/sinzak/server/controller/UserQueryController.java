package net.sinzak.server.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.config.auth.LoginUser;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.dto.respond.GetFollowDto;
import net.sinzak.server.service.UserQueryService;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
    @GetMapping(value ="/users/{userId}")
    public JSONObject getUser( @PathVariable("userId") Long userId,@ApiIgnore @LoginUser SessionUser user){
        JSONObject jsonObject = userQueryService.getUserProfile(userId, user);
        return jsonObject;
    }
    @ApiOperation(value ="팔로워리스트")
    @GetMapping(value ="/users/{userId}/followers")
    public ResponseEntity getFollowerList( @PathVariable("userId") Long userId) {
        List<GetFollowDto> getFollowDtoList =
               userQueryService.getFollowerDtoList(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(getFollowDtoList);
    }
    @ApiOperation(value ="팔로잉리스트")
    @GetMapping(value ="/users/{userId}/followings")
    public ResponseEntity getFollowingList(@PathVariable("userId") Long userId) {
        List<GetFollowDto> getFollowDtoList =
                userQueryService.getFollowingDtoList(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(getFollowDtoList);
    }




}
