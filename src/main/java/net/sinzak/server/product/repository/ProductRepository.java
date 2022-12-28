package net.sinzak.server.product.repository;

import net.sinzak.server.product.Product;
import net.sinzak.server.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Override
    Optional<Product> findById(Long aLong);

    @Query("select p from Product p order by p.id desc")
    List<Product> findAll();

    @Query("select p from Product p order by p.id desc")
    Page<Product> findAll(Pageable pageable);

    @Query("select p from Product p left join fetch p.productWishList where p.id = :id")
    Optional<Product> findByEmailFetchPW(@Param("id")Long id);   /** 해당 작품 찜을 누른 유저 목록까지 불러오기 **/

    @Query(value = "select * from product as p where p.category like %:category1% order by p.product_id desc limit :count", nativeQuery = true)
    List<Product> find1Recommend3(@Param("category1") String category1, @Param("count") int count);
    @Query(value = "select * from product as p where p.category like %:category1% or p.category like %:category2% order by p.product_id desc limit :count", nativeQuery = true)
    List<Product> find2Recommend3(@Param("category1") String category1, @Param("category2") String category2, @Param("count") int count);
    @Query(value = "select * from product as p where p.category like %:category1% or p.category like %:category2% or p.category like %:category3% order by p.product_id desc limit :count", nativeQuery = true)
    List<Product> find3Recommend3(@Param("category1") String category1, @Param("category2") String category2, @Param("category3") String category3, @Param("count") int count);

    @Query("select p from Product p where p.category like %:stack1% order by p.id desc")
    Page<Product> findBy1StacksDesc(Pageable pageable, @Param("stack1") String stack1);

    @Query("select p from Product p where p.category like %:stack1% or p.category like %:stack2% order by p.id desc")
    Page<Product> findBy2StacksDesc(Pageable pageable, @Param("stack1")String stack1, @Param("stack2")String stack2);

    @Query("select p from Product p where p.category like %:stack1% or p.category like %:stack2% or p.category like %:stack3% order by p.id desc")
    Page<Product> findBy3StacksDesc(Pageable pageable, @Param("stack1")String stack1, @Param("stack2")String stack2, @Param("stack3")String stack3);

}
