package net.sinzak.server.product.repository;

import net.sinzak.server.product.domain.ProductSell;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSellRepository extends JpaRepository<ProductSell, Long> {
}
