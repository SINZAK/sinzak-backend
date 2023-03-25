package net.sinzak.server.work.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.UserUtils;
import net.sinzak.server.common.dto.SuggestDto;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.common.error.UserNotLoginException;
import net.sinzak.server.common.resource.ApiDocumentResponse;
import net.sinzak.server.common.dto.ActionForm;
import net.sinzak.server.product.dto.ImageUrlDto;
import net.sinzak.server.product.dto.SellDto;
import net.sinzak.server.product.dto.ShowForm;
import net.sinzak.server.work.dto.WorkEditDto;
import net.sinzak.server.work.dto.WorkPostDto;
import net.sinzak.server.work.service.WorkService;
import org.json.simple.JSONObject;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "의뢰")
@RestController
@RequiredArgsConstructor
public class WorkController {

    private final WorkService workService;

    @ApiDocumentResponse
    @ApiOperation(value = "의뢰 모집 글 생성", notes =  "{\"success\":true, \"id\":52}\n해당 글의 id를 전해드리니 이 /works/{id}/image 에 넘겨주세요\n" +
            "category = portrait, illustration, logo, poster, design, editorial, label, other 주의점은 콤마로 구분하되 공백은 삽입X")
    @PostMapping(value = "/works/build", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public JSONObject makePost(@Valid @RequestBody WorkPostDto postDto) {
        return workService.makePost(postDto);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "의뢰 모집 글 이미지 연결")
    @PostMapping(value = "/works/{id}/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiImplicitParam(name = "multipartFile", dataType = "multipartFile",
            value = "파일 보내주시면 파일 s3서버에 저장 및, 해당 파일이 저장되어 있는 URL을 디비에 저장합니다")
    public JSONObject makePost(@PathVariable("id") Long workId, @RequestPart List<MultipartFile> multipartFile) {
        return workService.saveImageInS3AndWork(multipartFile, workId);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "의뢰 이미지 삭제", notes = "하나씩만 처리할게요, 썸네일(첫번째 사진)은 불가능하게 가시죠")
    @PostMapping(value = "/works/{id}/deleteimage")
    public JSONObject deletePostImage(@PathVariable("id") Long workId, @RequestBody ImageUrlDto dto) {
        return workService.deleteImage(workId, dto.getUrl());
    }

    @ApiDocumentResponse
    @ApiOperation(value = "의뢰 수정")
    @PostMapping(value = "/works/{id}/edit")
    public JSONObject editPost(@PathVariable("id") Long workId, @RequestBody WorkEditDto editDto) {
        return workService.editPost(workId, editDto);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "의뢰 삭제")
    @PostMapping(value = "/works/{id}/delete")
    public JSONObject deletePost(@PathVariable("id") Long workId) {
        return workService.deletePost(workId);
    }

    @PostMapping("/works/{id}")
    @ApiOperation(value = "의뢰 상세 조회")
    public JSONObject showWorks(@PathVariable Long id) {
        try{
            UserUtils.getContextHolderId();
            return workService.showDetailForUser(id);
        }
        catch (UserNotFoundException | UserNotLoginException e){
            return workService.showDetailForGuest(id); /** 비회원용 **/
        }
    }

    @ApiDocumentResponse
    @PostMapping("/works/wish")
    @ApiOperation(value = "의뢰 찜")
    public JSONObject wish(@RequestBody ActionForm form) {
        return workService.wish(form);
    }

    @ApiDocumentResponse
    @PostMapping("/works/likes")
    @ApiOperation(value = "의뢰 좋아요", notes = "{\"success\":true, \"isfav\" : true} 이런식으로 보냅니다. 요청 이후 좋아요 버튼이 어떻게 되어있어야 하는지 알려주기위해서")
    public JSONObject likes(@RequestBody ActionForm form) {
        return workService.likes(form);
    }

    @ApiDocumentResponse
    @PostMapping("/works/sell")
    @ApiOperation(value = "모집 완료", notes = "해당 의뢰 모집 완료 설정!")
    public JSONObject sell(@RequestBody SellDto dto) {
        return workService.sell(dto);
    }


    @ApiDocumentResponse
    @PostMapping("/works/suggest")
    @ApiOperation(value = "의뢰 가격제안")
    public JSONObject suggest(@RequestBody SuggestDto dto) {
        return workService.suggest(dto);
    }

    @ApiOperation(value = "의뢰 글 모음")
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
    public PageImpl<ShowForm> showWorks(@RequestParam(required=false, defaultValue="") String search, @RequestParam(required=false, defaultValue="") List<String> categories, @RequestParam(required=false, defaultValue="recommend") String align, @RequestParam(required=false, defaultValue="true") Boolean employment, @ApiIgnore Pageable pageable) {
        try{
            UserUtils.getContextHolderId();
            return workService.workListForUser(search, categories, align, employment, pageable);
        }
        catch (UserNotFoundException | UserNotLoginException e) {
            return workService.workListForGuest(search, categories, align, employment, pageable);
        }
    }

}
