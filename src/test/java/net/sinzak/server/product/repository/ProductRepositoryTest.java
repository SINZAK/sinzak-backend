package net.sinzak.server.product.repository;

import net.sinzak.server.product.domain.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@SpringBootTest
class ProductRepositoryTest {
    @Autowired ProductRepository productRepository;

//    @Test
//    @DisplayName("쿼리 테스트")
//    void queryTest(Pageable pageable){
//        Page<Product> products = productRepository.findAllCompleteBy(false, pageable);
//        Assertions.assertThat(products.getTotalElements()).isEqualTo(12);
//        Assertions.assertThat(products.getContent().size()).isEqualTo(12);
//        Page<Product> products2 = productRepository.findAllCompleteBy(true, pageable);
//        Assertions.assertThat(products.getTotalElements()).isEqualTo(12);
//        Assertions.assertThat(products.getContent().size()).isEqualTo(12);
//    }

}