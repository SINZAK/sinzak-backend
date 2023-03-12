package net.sinzak.server.product.repository;

import net.sinzak.server.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>{

    @Query("select p from Product p where p.isDeleted =false order by p.id desc") //이미 인덱스로 정렬되어있음
    List<Product> findAllProductNotDeleted();

    @Query("select p from Product p where p.isDeleted =false order by p.id desc")
    Page<Product> findAll(Pageable pageable);

    @Query("select p from Product p left join fetch p.productWishList left join fetch p.user where p.id = :id")
    Optional<Product> findByIdFetchProductWishAndUser(@Param("id")Long id);   /** 해당 작품 찜을 누른 유저 목록까지 불러오기 **/


    @Query("select p from Product p left join fetch p.chatRooms where p.id = :id and p.isDeleted = false ")
    Optional<Product> findByIdFetchChatRooms(@Param("id") Long id);

}
