package net.sinzak.server.product.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.resource.ApiDocumentResponse;
import net.sinzak.server.config.auth.LoginUser;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.product.dto.DetailForm;
import net.sinzak.server.product.dto.SellDto;
import net.sinzak.server.product.dto.ShowForm;
import net.sinzak.server.product.service.ProductService;
import net.sinzak.server.product.dto.ProductPostDto;
import net.sinzak.server.common.dto.WishForm;
import net.sinzak.server.user.domain.User;
import org.json.simple.JSONObject;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Api(tags = "작품")
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @ApiDocumentResponse
    @ApiOperation(value = "작품 판매 글 생성")
    @PostMapping(value = "/products/build", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "postDto", dataType = "json", value = "{\n" +
                    "\"category\": \"작품 카테고리\",\n" +
                    "\"content\": \"작품 판매글 내용\",\n" +
                    "\"field\": \"작품 분야\",\n" +
                    "\"height\": 50,\n" +
                    "\"price\": 30000,\n" +
                    "\"suggest\": false,\n" +
                    "\"title\": \"작품 판매글 제목\",\n" +
                    "\"vertical\": 150,\n" +
                    "\"width\": 120\n" +
                    "}"),
            @ApiImplicitParam(name = "multipartFile", dataType = "multipartFile",
                    value = "파일 보내주시면 파일 s3서버에 저장 및, 해당 파일이 저장되어 있는 URL을 디비에 저장합니다")
    })
    public JSONObject makeProductPost(@AuthenticationPrincipal User user, @RequestPart ProductPostDto postDto, @RequestPart List<MultipartFile> multipartFile) {
        return productService.makePost(user, postDto, multipartFile);
    }

    @PostMapping("/products/{id}")
    @ApiOperation(value = "작품 상세 조회")
    public DetailForm showProject(@PathVariable Long id, @AuthenticationPrincipal User user) {
        try{
            return productService.showDetail(id,user);
        }
        catch (NullPointerException e){
            return productService.showDetail(id); /** 비회원용 **/
        }
    }

    @ApiDocumentResponse
    @PostMapping("/products/wish")
    @ApiOperation(value = "작품 찜")
    public JSONObject wish(@AuthenticationPrincipal User user, @RequestBody WishForm form) {
        return productService.wish(user, form);
    }

    @ApiDocumentResponse
    @PostMapping("/products/likes")
    @ApiOperation(value = "작품 좋아요")
    public JSONObject likes(@AuthenticationPrincipal User user, @RequestBody WishForm form) {
        return productService.likes(user, form);
    }

    @ApiDocumentResponse
    @PostMapping("/products/sell")
    @ApiOperation(value = "작품 판매", notes = "회원의 구매목록에 추가, 해당 작품 판매완료 설정")
    public JSONObject sell(@AuthenticationPrincipal User user, @RequestBody SellDto dto) {
        return productService.sell(user, dto);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "작품 홈")
    @PostMapping("/home/products")
    public JSONObject showHomeProduct(@ApiIgnore @AuthenticationPrincipal User user) {
        try {
            return productService.showHome(user);
        }
        catch (NullPointerException e) {
            return productService.showHome(); /** 비회원용 **/
        }
    }

    @ApiOperation(value = "작품 추천 상세페이지")
    @PostMapping("/home/recommend")
    public List<ShowForm> showRecommendDetail(@AuthenticationPrincipal User user) {
        return productService.showRecommendDetail(user);
    }

    @ApiOperation(value = "작품 추천 상세페이지")
    @PostMapping("/home/following")
    public List<ShowForm> showFollowingDetail(@AuthenticationPrincipal User user) {
        return productService.showFollowingDetail(user);
    }

    @ApiOperation(value = "마켓 작품")
    @PostMapping("/market/products")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "파라미터 형식으로 전달해주세요 (0..N) \nex) http://localhost:8080/market/products?page=3&size=5&stacks=orient,western\nhttp://localhost:8080/market/products?page=0&size=5&stacks=orient&stacks=western  둘 다 가능합니다", defaultValue = "0"),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "3", defaultValue = "5"),
            @ApiImplicitParam(name = "align", dataType = "string", paramType = "query",
                    value = "정렬 기준\nrecommend - 신작추천순\n" +
                            "popular - 인기순(좋아요)\n" +
                            "recent - 최신순\n" +
                            "low - 낮은가격순\n" +
                            "high - 높은가격순", defaultValue = "recommend"),
            @ApiImplicitParam(name = "categories", dataType = "string", paramType = "query",
                    value = "categories(최대 3개)\n" +
                            "painting - 회화일반\n" +
                            "orient - 동양화\n" +
                            "sculpture - 조소\n" +
                            "print\n - 판화" +
                            "craft\n - 공예" +
                            "other\n - 기타", defaultValue = "생략하기")
    })
    public PageImpl<ShowForm> showMarketProduct(@AuthenticationPrincipal User user, @RequestParam(required=false, defaultValue="") List<String> categories, @RequestParam(required=false, defaultValue="recommend") String align, @ApiIgnore Pageable pageable) {
        try{
            return productService.productListForUser(user, categories, align, pageable);
        }
        catch (NullPointerException e){
            return productService.productListForGuest(categories,align,pageable);
        }
    }

//    @ExceptionHandler(NullPointerException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    protected ErrorResponse handleException1() {
//        return ErrorResponse.of(HttpStatus.BAD_REQUEST, "존재하지 않는 유저");
//    }
//
//    @ExceptionHandler(NoSuchElementException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    protected ErrorResponse handleException2() {
//        return ErrorResponse.of(HttpStatus.BAD_REQUEST, "존재하지 않는 값을 조회중입니다.");
//    }
}
