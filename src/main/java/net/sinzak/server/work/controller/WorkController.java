package net.sinzak.server.work.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.dto.SuggestDto;
import net.sinzak.server.common.resource.ApiDocumentResponse;
import net.sinzak.server.common.dto.ActionForm;
import net.sinzak.server.product.dto.ShowForm;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.work.dto.DetailWorkForm;
import net.sinzak.server.work.dto.WorkPostDto;
import net.sinzak.server.common.error.ErrorResponse;
import net.sinzak.server.work.service.WorkService;
import org.json.simple.JSONObject;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.NoSuchElementException;

@Api(tags = "외주")
@RestController
@RequiredArgsConstructor
public class WorkController {

    private final WorkService workService;

    @ApiDocumentResponse
    @ApiOperation(value = "외주 모집 글 생성", notes =  "{\"success\":true, \"id\":52}\n해당 글의 id를 전해드리니 이 /works/{id}/image 에 넘겨주세요\n" +
            "category = portrait, illustration, logo, poster, design, editorial, label, other 주의점은 콤마로 구분하되 공백은 삽입X")
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

    @PostMapping("/works/{id}")
    @ApiOperation(value = "외주 상세 조회")
    public JSONObject showProject(@PathVariable Long id, @AuthenticationPrincipal User user) {
        try{
            return workService.showDetail(id,user);
        }
        catch (NullPointerException e){
            return workService.showDetail(id); /** 비회원용 **/
        }
    }

    @ApiDocumentResponse
    @PostMapping("/works/wish")
    @ApiOperation(value = "작품 찜")
    public JSONObject wish(@AuthenticationPrincipal User user, @RequestBody ActionForm form) {
        return workService.wish(user, form);
    }

    @ApiDocumentResponse
    @PostMapping("/works/likes")
    @ApiOperation(value = "외주 좋아요", notes = "{\"success\":true, \"isfav\" : true} 이런식으로 보냅니다. 요청 이후 좋아요 버튼이 어떻게 되어있어야 하는지 알려주기위해서")
    public JSONObject likes(@AuthenticationPrincipal User user, @RequestBody ActionForm form) {
        return workService.likes(user, form);
    }

    @ApiDocumentResponse
    @PostMapping("/works/suggest")
    @ApiOperation(value = "외주 가격제안")
    public JSONObject suggest(@AuthenticationPrincipal User user, @RequestBody SuggestDto dto) {
        return workService.suggest(user, dto);
    }

    @ApiOperation(value = "외주 글 모음")
    @PostMapping("/works")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "파라미터 형식으로 전달해주세요 (0..N) \nex) http://localhost:8080/api/works?page=3&size=5&stacks=logo,design\nhttp://localhost:8080/api/works?page=0&size=5&stacks=design&stacks=logo  둘 다 가능합니다", defaultValue = "0"),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "3", defaultValue = "5"),
            @ApiImplicitParam(name = "align", dataType = "string", paramType = "query",
                    value = "정렬 기준\n" +"recent - 최신순\n"+
                            "recommend - 신작추천순\n" , defaultValue = "recent"),
            @ApiImplicitParam(name = "categories", dataType = "string", paramType = "query",
                    value = "categories(최대 3개)\n" +
                            "portrait\n" +
                            "illustration\n" +
                            "logo\n" +
                            "poster\n" +
                            "design\n" +
                            "editorial\n" +
                            "label\n" +
                            "other", defaultValue = ""),
            @ApiImplicitParam(name = "search", dataType = "string", paramType = "query",
                    value = "String 값으로 주시고 최소 2글자 이상은 받아야 합니다. contain 메서드로 db에서 검색할 예정.")
    })
    public PageImpl<ShowForm> showWorks(@AuthenticationPrincipal User user, @RequestParam(required=false, defaultValue="") String search, @RequestParam(required=false, defaultValue="") List<String> categories, @RequestParam(required=false, defaultValue="recent") String align, @RequestParam(required=false, defaultValue="true") Boolean employment, @ApiIgnore Pageable pageable) {
        try{
            return workService.workListForUser(user, search, categories, align, employment, pageable);
        }
        catch (NullPointerException e){
            return workService.workListForGuest(search, categories,align, employment, pageable);
        }
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
