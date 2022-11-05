package net.sinzak.server.repository;

import net.sinzak.server.domain.ProductWish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductWishRepository extends JpaRepository<ProductWish,Long> {
}
