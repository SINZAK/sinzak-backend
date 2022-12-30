package net.sinzak.server.product.repository;

import net.sinzak.server.product.domain.ProductWish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductWishRepository extends JpaRepository<ProductWish,Long> {
}
