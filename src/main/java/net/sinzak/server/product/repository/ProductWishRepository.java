package net.sinzak.server.product.repository;

import net.sinzak.server.product.domain.ProductWish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductWishRepository extends JpaRepository<ProductWish, Long> {

    @Query("select pw from ProductWish pw left join fetch pw.product where pw.user.id = :userId and pw.product.isDeleted =false")
    List<ProductWish> findByUserIdFetchProduct(@Param("userId") Long userId);
}
