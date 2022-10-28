package net.sinzak.server.controller;

import lombok.RequiredArgsConstructor;
import net.sinzak.server.service.ProductService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
}
