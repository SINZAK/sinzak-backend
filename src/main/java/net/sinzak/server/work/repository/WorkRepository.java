package net.sinzak.server.work.repository;

import net.sinzak.server.product.domain.Product;
import net.sinzak.server.work.domain.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WorkRepository extends JpaRepository<Work, Long> {

    @Query("select w from Work w left join fetch w.workWishList left join fetch w.user where w.id = :id and w.isDeleted = false")
    Optional<Work> findByIdFetchWorkNotDeletedWishAndUser(@Param("id")Long id);   /** 해당 외주 찜을 누른 유저 목록까지 불러오기 **/

    @Query("select w from Work w order by w.id desc")
    Page<Work> findAll(Pageable pageable);

    @Query("select w from Work w where w.id = :id and w.isDeleted = false")
    Optional<Work> findByIdNotDeleted(@Param("id") Long id);

    @Query("select w from Work w left join fetch w.images where w.id = :id")
    Optional<Work> findByIdFetchImages(@Param("id")Long id);

    @Query("select w from Work w left join fetch w.chatRooms where w.id = :id and w.isDeleted = false")
    Optional<Work> findByIdFetchChatRooms(@Param("id") Long id);

}
