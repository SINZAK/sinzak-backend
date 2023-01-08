package net.sinzak.server.work.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.resource.ApiDocumentResponse;
import net.sinzak.server.config.auth.LoginUser;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.common.dto.ActionForm;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.work.dto.WorkPostDto;
import net.sinzak.server.common.error.ErrorResponse;
import net.sinzak.server.work.service.WorkService;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

@Api(tags = "외주")
@RestController
@RequiredArgsConstructor
public class WorkController {

    private final WorkService workService;

    @ApiDocumentResponse
    @ApiOperation(value = "외주 모집 글 생성")
    @PostMapping(value = "/works/build", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public JSONObject makeWorkPost(@AuthenticationPrincipal User user, @RequestBody WorkPostDto postDto) {
        return workService.makePost(user, postDto);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "외주 모집 글 이미지 연결")
    @PostMapping(value = "/works/{id}/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiImplicitParam(name = "multipartFile", dataType = "multipartFile",
            value = "파일 보내주시면 파일 s3서버에 저장 및, 해당 파일이 저장되어 있는 URL을 디비에 저장합니다")
    public JSONObject makeProductPost(@AuthenticationPrincipal User user, @PathVariable("id") Long workId, @RequestPart List<MultipartFile> multipartFile) {
        return workService.saveImageInS3AndWork(user, multipartFile, workId);
    }
//
//    @ApiDocumentResponse
//    @PostMapping("/works/wish")
//    @ApiOperation(value = "작품 찜")
//    public JSONObject wish(@LoginUser SessionUser user, @RequestBody ActionForm form) {
//        return workService.wish(user, form);
//    }
//
//


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
