package net.sinzak.server.product.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.config.auth.LoginUser;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.product.dto.SellDto;
import net.sinzak.server.product.service.ProductService;
import net.sinzak.server.product.dto.ProductPostDto;
import net.sinzak.server.common.dto.WishForm;
import net.sinzak.server.user.domain.User;
import org.json.simple.JSONObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public JSONObject showHomeProduct(@AuthenticationPrincipal User user) {
        return productService.showHome(user);
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
