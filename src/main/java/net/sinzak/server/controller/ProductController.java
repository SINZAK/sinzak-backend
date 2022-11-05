package net.sinzak.server.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.config.auth.LoginUser;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.domain.User;
import net.sinzak.server.dto.ProductPostDto;
import net.sinzak.server.dto.WishForm;
import net.sinzak.server.dto.WorkPostDto;
import net.sinzak.server.error.ErrorResponse;
import net.sinzak.server.service.ProductService;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @ApiOperation(value = "작품 판매 글 생성")
    @PostMapping("/products/build")
    public JSONObject makeProductPost(@LoginUser SessionUser user, /*@RequestBody*/ProductPostDto postDto) {
        return productService.makeProductPost(user, postDto); //해당 유저의 작품 글 리스트까지 fetch해서 가져오기.
    }

    @PostMapping("/products/wish")
    @ApiOperation(value = "작품 찜")
    public JSONObject wish(@LoginUser SessionUser user, @RequestBody WishForm form) {
        return productService.wish(user, form);
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
