package net.sinzak.server.work.repository;

import net.sinzak.server.product.domain.Product;
import net.sinzak.server.work.domain.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WorkRepository extends JpaRepository<Work, Long> {
    @Query("select w from Work w left join fetch w.workWishList left join fetch w.user where w.id = :id")
    Optional<Work> findByIdFetchPWUser(@Param("id")Long id);   /** 해당 외주 찜을 누른 유저 목록까지 불러오기 **/
}
