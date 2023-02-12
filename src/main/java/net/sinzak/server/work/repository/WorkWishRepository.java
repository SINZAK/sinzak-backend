package net.sinzak.server.work.repository;

import net.sinzak.server.work.domain.WorkWish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkWishRepository extends JpaRepository<WorkWish, Long> {

    @Query("select ww from WorkWish ww left join fetch ww.work where ww.user.id = :userId")
    List<WorkWish> findByUserIdFetchWork(@Param("userId") Long userId);
}
