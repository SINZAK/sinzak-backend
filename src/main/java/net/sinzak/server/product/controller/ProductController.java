package net.sinzak.server.product.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.dto.DetailForm;
import net.sinzak.server.common.dto.SuggestDto;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.common.resource.ApiDocumentResponse;
import net.sinzak.server.product.dto.*;
import net.sinzak.server.product.service.ProductService;
import net.sinzak.server.common.dto.ActionForm;
import net.sinzak.server.user.domain.User;
import org.json.simple.JSONObject;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(tags = "작품")
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @ApiDocumentResponse
    @ApiOperation(value = "작품 판매 글 생성")
    @PostMapping(value = "/products/build", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public JSONObject makeProductPost(@AuthenticationPrincipal User user, @RequestBody ProductPostDto buildDto) {
        return productService.makePost(user, buildDto);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "작품 판매 글 이미지 연결")
    @PostMapping(value = "/products/{id}/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiImplicitParam(name = "multipartFile", dataType = "multipartFile",
            value = "파일 보내주시면 파일 s3서버에 저장 및, 해당 파일이 저장되어 있는 URL을 디비에 저장합니다")
    public JSONObject makeProductPost(@AuthenticationPrincipal User user, @PathVariable("id") Long productId, @RequestPart List<MultipartFile> multipartFile) {
        return productService.saveImageInS3AndProduct(user, multipartFile, productId);
    }

    @PostMapping("/products/{id}")
    @ApiOperation(value = "작품 상세 조회")
    public DetailProductForm showProject(@PathVariable Long id, @AuthenticationPrincipal User user) {
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
    public JSONObject wish(@AuthenticationPrincipal User user, @RequestBody ActionForm form) {
        return productService.wish(user, form);
    }

    @ApiDocumentResponse
    @PostMapping("/products/likes")
    @ApiOperation(value = "작품 좋아요", notes = "{\"success\":true, \"isfav\" : true} 이런식으로 보냅니다. 요청 이후 좋아요 버튼이 어떻게 되어있어야 하는지 알려주기위해서")
    public JSONObject likes(@AuthenticationPrincipal User user, @RequestBody ActionForm form) {
        return productService.likes(user, form);
    }

    @ApiDocumentResponse
    @PostMapping("/products/trading")
    @ApiOperation(value = "작품 거래중")
    public JSONObject trading(@RequestBody ActionForm form) {
        return productService.trading(form);
    }

    @ApiDocumentResponse
    @PostMapping("/products/sell")
    @ApiOperation(value = "작품 판매", notes = "회원의 구매목록에 추가, 해당 작품 판매완료 설정")
    public JSONObject sell(@AuthenticationPrincipal User user, @RequestBody SellDto dto) {
        return productService.sell(user, dto);
    }

    @ApiDocumentResponse
    @PostMapping("/products/suggest")
    @ApiOperation(value = "작품 가격제안")
    public JSONObject suggest(@AuthenticationPrincipal User user, @RequestBody SuggestDto dto) {
        return productService.suggest(user, dto);
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

    @ApiOperation(value = "홈 - 추천 더보기")
    @PostMapping("/home/recommend")
    public List<ShowForm> showRecommendDetail(@AuthenticationPrincipal User user) {
        return productService.showRecommendDetail(user);
    }

    @ApiOperation(value = "홈 - 팔로잉 더보기")
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
                            "print - 판화\n" +
                            "craft - 공예\n" +
                            "other - 기타", defaultValue = "")
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
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    protected JSONObject handleUserNotFoundException() {
        return PropertyUtil.responseMessage("존재하지 않는 유저입니다.");
    }

}
