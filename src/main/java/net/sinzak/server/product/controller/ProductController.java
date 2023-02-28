package net.sinzak.server.product.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.dto.SuggestDto;
import net.sinzak.server.common.resource.ApiDocumentResponse;
import net.sinzak.server.config.auth.AuthUser;
import net.sinzak.server.product.dto.*;
import net.sinzak.server.product.service.ProductService;
import net.sinzak.server.common.dto.ActionForm;
import net.sinzak.server.user.domain.User;
import org.json.simple.JSONObject;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    @ApiOperation(value = "작품 판매 글 생성",notes = "{\"success\":true, \"id\":52}\n해당 글의 id를 전해드리니 이 /products/{id}/image 에 넘겨주세요\n" +
            "category = painting,orient,sculpture,print,craft,other")
    @PostMapping(value = "/products/build", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public JSONObject makePost(@AuthenticationPrincipal User user, @RequestBody ProductPostDto buildDto) {
        return productService.makePost(user, buildDto);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "작품 판매 글 이미지 연결")
    @PostMapping(value = "/products/{id}/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiImplicitParam(name = "multipartFile", dataType = "multipartFile",
            value = "파일 보내주시면 파일 s3서버에 저장 및, 해당 파일이 저장되어 있는 URL을 디비에 저장합니다")
    public JSONObject makePost(@AuthenticationPrincipal User user, @PathVariable("id") Long productId, @RequestPart List<MultipartFile> multipartFile) {
        return productService.saveImageInS3AndProduct(user, multipartFile, productId);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "작품 이미지 삭제", notes = "하나씩만 처리할게요, 썸네일(첫번째 사진)은 불가능하게 가시죠")
    @PostMapping(value = "/products/{id}/deleteimage")
    public JSONObject deleteProductImage(@AuthenticationPrincipal User user, @PathVariable("id") Long productId, @RequestBody ImageUrlDto dto) {
        return productService.deleteImage(user, productId, dto.getUrl());
    }

    @ApiDocumentResponse
    @ApiOperation(value = "작품 수정")
    @PostMapping(value = "/products/{id}/edit")
    public JSONObject editPost(@AuthenticationPrincipal User user, @PathVariable("id") Long productId, @RequestBody ProductEditDto editDto) {
        return productService.editPost(user, productId, editDto);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "작품 삭제")
    @PostMapping(value = "/products/{id}/delete")
    public JSONObject deletePost(@AuthenticationPrincipal User user, @PathVariable("id") Long productId) {
        return productService.deletePost(user, productId);
    }


    @PostMapping("/products/{id}")
    @ApiOperation(value = "작품 상세 조회")
    public JSONObject showProject(@PathVariable Long id, @AuthenticationPrincipal User user) {
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
    public JSONObject sell(@AuthUser User user, @RequestBody SellDto dto) {
        return productService.sell(user, dto);
    }

    @ApiDocumentResponse
    @PostMapping("/products/suggest")
    @ApiOperation(value = "작품 가격제안")
    public JSONObject suggest(@AuthUser User user, @RequestBody SuggestDto dto) {
        return productService.suggest(user, dto);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "작품 홈")
    @PostMapping("/home/products")
    public JSONObject showHomeProduct(@AuthenticationPrincipal User user) {
        try {
            return productService.showHome(user);
        }
        catch (NullPointerException e) {
            return productService.showHome(); /** 비회원용 **/
        }
    }

    @ApiOperation(value = "홈 - 추천 더보기")
    @PostMapping("/home/recommend")
    public JSONObject showRecommendDetail(@AuthenticationPrincipal User user) {
        return productService.showRecommendDetail(user);
    }

    @ApiOperation(value = "홈 - 팔로잉 더보기")
    @PostMapping("/home/following")
    public JSONObject showFollowingDetail(@AuthenticationPrincipal User user) {
        return productService.showFollowingDetail(user);
    }

    @ApiOperation(value = "마켓 작품")
    @PostMapping("/products")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "파라미터 형식으로 전달해주세요 (0..N) \nex) http://localhost:8080/api/products?page=3&size=5&stacks=orient,western\nhttp://localhost:8080/api/products?page=0&size=5&stacks=orient&stacks=western&sale=true  둘 다 가능합니다", defaultValue = "0"),
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
                            "other - 기타", defaultValue = ""),
            @ApiImplicitParam(name = "sale", dataType = "boolean", paramType = "query",
                    value = "sale = false -> 모든 작품(디폴트)" +
                            "sale = true -> 판매중인 작품만(거래중도 포함, 거래완료는 X)" +
                            "생략시 판매중인 작품만 보기 버튼이 체크 되지 않은 상태라고 생각하시면 됩니다  어떤 파라미터명을써도 딱 맞아떨어지는게 없어서 sale로 갈게요", defaultValue = "false"),
            @ApiImplicitParam(name = "search", dataType = "string", paramType = "query",
                    value = "String 값으로 주시고 최소 2글자 이상은 받아야 합니다. contain 메서드로 db에서 검색할 예정.")
    })
    public PageImpl<ShowForm> showMarketProduct(@AuthenticationPrincipal User user, @RequestParam(required=false, defaultValue="") String search, @RequestParam(required=false, defaultValue="") List<String> categories, @RequestParam(required=false, defaultValue="recommend") String align, @RequestParam(required=false, defaultValue="false") Boolean sale, @ApiIgnore Pageable pageable) {
        try{
            return productService.productListForUser(user, search, categories, align, sale, pageable);
        }
        catch (NullPointerException e){
            return productService.productListForGuest(search, categories, align, sale, pageable);
        }
    }


//    @ExceptionHandler(UserNotFoundException.class)
//    @ResponseStatus(HttpStatus.OK)
//    protected JSONObject handleUserNotFoundException() {
//    return PropertyUtil.responseMessage("존재하지 않는 유저입니다.");
//}
//
//    @ExceptionHandler(InstanceNotFoundException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    protected JSONObject handleInstanceNotFoundException() {return PropertyUtil.responseMessage("존재하지 않는 객체입니다.");}

}
