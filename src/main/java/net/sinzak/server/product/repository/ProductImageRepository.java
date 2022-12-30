package net.sinzak.server.product.repository;

import net.sinzak.server.product.domain.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage,Long> {
}
