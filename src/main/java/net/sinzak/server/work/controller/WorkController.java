package net.sinzak.server.work.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.resource.ApiDocumentResponse;
import net.sinzak.server.config.auth.LoginUser;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.common.dto.WishForm;
import net.sinzak.server.work.dto.WorkPostDto;
import net.sinzak.server.common.error.ErrorResponse;
import net.sinzak.server.work.service.WorkService;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@Api(tags = "외주")
@RestController
@RequiredArgsConstructor
public class WorkController {

    private final WorkService workService;

    @ApiDocumentResponse
    @ApiOperation(value = "외주 모집 글 생성")
    @PostMapping("/works/build")
    public JSONObject makeWorkPost(@LoginUser SessionUser user, /*@RequestBody*/ WorkPostDto postDto) {
        return workService.makeWorkPost(user, postDto);
    }

    @ApiDocumentResponse
    @PostMapping("/works/wish")
    @ApiOperation(value = "작품 찜")
    public JSONObject wish(@LoginUser SessionUser user, @RequestBody WishForm form) {
        return workService.wish(user, form);
    }




    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleException1() {
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, "존재하지 않는 유저");
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleException2() {
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, "존재하지 않는 값을 조회중입니다.");
    }
}
