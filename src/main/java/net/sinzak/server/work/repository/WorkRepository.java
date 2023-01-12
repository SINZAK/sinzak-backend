package net.sinzak.server.work.repository;

import net.sinzak.server.work.domain.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WorkRepository extends JpaRepository<Work, Long> {

    @Query("select w from Work w left join fetch w.workWishList left join fetch w.user where w.id = :id")
    Optional<Work> findByIdFetchWorkWishAndUser(@Param("id")Long id);   /** 해당 외주 찜을 누른 유저 목록까지 불러오기 **/

    @Query("select w from Work w order by w.id desc")
    Page<Work> findAll(Pageable pageable);

    @Query("select w from Work w where w.employment = :employment order by w.id desc")
    Page<Work> findAll(boolean employment, Pageable pageable);

    @Query("select w from Work w left join Fetch w.user left join fetch w.chatRooms where w.id = :id")
    Optional<Work> findByIdFetchUserAAndChatRooms(@Param("id") Long id);

}
