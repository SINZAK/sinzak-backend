package net.sinzak.server.product.repository;

import net.sinzak.server.product.domain.ProductSuggest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductSuggestRepository extends JpaRepository<ProductSuggest, Long> {
    @Query("select p from ProductSuggest p where p.user.id = :userId and p.product.id = :productId and p.product.isDeleted =false ")
    Optional<ProductSuggest> findByUserIdAndProductId(@Param("userId")Long userId, @Param("productId")Long productId);
}