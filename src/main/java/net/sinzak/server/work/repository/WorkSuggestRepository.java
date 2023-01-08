package net.sinzak.server.work.repository;

import net.sinzak.server.product.domain.ProductSuggest;
import net.sinzak.server.work.domain.WorkSuggest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WorkSuggestRepository extends JpaRepository<WorkSuggest, Long> {
    @Query("select w from WorkSuggest w where w.user.id = :userId and w.work.id = :workId")
    Optional<WorkSuggest> findByUserIdAndWorkId(@Param("userId")Long userId, @Param("workId")Long workId);
}
