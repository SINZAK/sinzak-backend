package net.sinzak.server.product.repository;

import net.sinzak.server.product.domain.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    @Query("select i from ProductImage i where i.product.id = :id")
    List<ProductImage> findByProductId(@Param("id") Long id);
}
