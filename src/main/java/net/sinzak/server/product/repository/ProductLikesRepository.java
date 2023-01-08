package net.sinzak.server.product.repository;

import net.sinzak.server.product.domain.ProductLikes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductLikesRepository extends JpaRepository<ProductLikes,Long> {
}
