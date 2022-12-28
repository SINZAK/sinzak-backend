package net.sinzak.server.product.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.config.auth.LoginUser;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.product.dto.SellDto;
import net.sinzak.server.product.dto.ShowForm;
import net.sinzak.server.product.service.ProductService;
import net.sinzak.server.product.dto.ProductPostDto;
import net.sinzak.server.common.dto.WishForm;
import net.sinzak.server.user.domain.User;
import org.json.simple.JSONObject;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @ApiOperation(value = "작품 판매 글 생성")
    @PostMapping("/products/build")
    public JSONObject makeProductPost(@LoginUser SessionUser user, /*@RequestBody*/ProductPostDto postDto) {
        return productService.makePost(user, postDto); //해당 유저의 작품 글 리스트까지 fetch해서 가져오기.
    }

    @PostMapping("/products/wish")
    @ApiOperation(value = "작품 찜")
    public JSONObject wish(@LoginUser SessionUser user, @RequestBody WishForm form) {
        return productService.wish(user, form);
    }

    @PostMapping("/products/likes")
    @ApiOperation(value = "작품 좋아요")
    public JSONObject likes(@LoginUser SessionUser user, @RequestBody WishForm form) {
        return productService.likes(user, form);
    }

    @PostMapping("/products/sell")
    @ApiOperation(value = "작품 판매", notes = "회원의 구매목록에 추가, 해당 작품 판매완료 설정")
    public JSONObject sell(@LoginUser SessionUser user, @RequestBody SellDto dto) {
        return productService.sell(user, dto);
    }

    @ApiOperation(value = "작품 홈")
    @PostMapping("/home/products")
    public JSONObject showHomeProduct(@ApiIgnore @AuthenticationPrincipal User user) {
        return productService.showHome(user);
    }

    @ApiOperation(value = "작품 추천 상세페이지")
    @PostMapping("/home/recommend")
    public List<ShowForm> showRecommendDetail(@ApiIgnore @AuthenticationPrincipal User user) {
        return productService.showRecommendDetail(user);
    }

    @ApiOperation(value = "작품 추천 상세페이지")
    @PostMapping("/home/following")
    public List<ShowForm> showFollowingDetail(@ApiIgnore @AuthenticationPrincipal User user) {
        return productService.showFollowingDetail(user);
    }

    @ApiOperation(value = "마켓 작품")
    @PostMapping("/market/products")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "파라미터 형식으로 전달해주세요 (0..N) ex) http://localhost:8080/market/products?page=3&size=5", defaultValue = "0"),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "Number of records per page.", defaultValue = "5")
    })
    public PageImpl<ShowForm> showMarketProduct(@AuthenticationPrincipal User user, @RequestParam(required=false, defaultValue="") List<String> stacks, @ApiIgnore Pageable pageable) {
        try{
            return productService.productListForUser(user, stacks, pageable);
        }
        catch (NullPointerException e){
            return productService.productListForGuest(stacks,pageable);
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
