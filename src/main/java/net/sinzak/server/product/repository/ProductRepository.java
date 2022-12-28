package net.sinzak.server.product.repository;

import net.sinzak.server.product.Product;
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

    @Query(value = "select * from product as p order by p.product_id desc limit 3",nativeQuery = true)
    List<Product> findTop3Desc();

    @Query(value = "select * from product as p where p.category like %:category1% order by p.product_id desc limit :count", nativeQuery = true)
    List<Product> find1Recommend3(@Param("category1") String category1, @Param("count") int count);
    @Query(value = "select * from product as p where p.category like %:category1% or p.category like %:category2% order by p.product_id desc limit :count", nativeQuery = true)
    List<Product> find2Recommend3(@Param("category1") String category1, @Param("category2") String category2, @Param("count") int count);
    @Query(value = "select * from product as p where p.category like %:category1% or p.category like %:category2% or p.category like %:category3% order by p.product_id desc limit :count", nativeQuery = true)
    List<Product> find3Recommend3(@Param("category1") String category1, @Param("category2") String category2, @Param("category3") String category3, @Param("count") int count);


//    @Query(value = "select * from product as p where p.category like %:category1% order by p.product_id desc limit 50", nativeQuery = true)
//    List<Product> find1RecommendDetail50(@Param("category1") String category1);
//    @Query(value = "select * from product as p where p.category like %:category1% or p.category like %:category2% order by p.product_id desc limit 50", nativeQuery = true)
//    List<Product> find2RecommendDetail50(@Param("category1") String category1, @Param("category2") String category2);
//    @Query(value = "select * from product as p where p.category like %:category1% or p.category like %:category2% or p.category like %:category3% order by p.product_id desc limit 50", nativeQuery = true)
//    List<Product> find3RecommendDetail50(@Param("category1") String category1, @Param("category2") String category2, @Param("category3") String category3);
}
