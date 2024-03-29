package net.sinzak.server.user.repository;

import net.sinzak.server.user.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("select r from Report  r where (r.opponentUser.id = :opponentUserId and r.user.id = :userId) or(r.opponentUser.id = :userId and r.user.id = :opponentUserId)")
    List<Report> findByUserIdAndOpponentUserIdBoth(@Param("userId") Long userId, @Param("opponentUserId") Long opponentUserId);

    @Query("select r from Report r left join fetch r.opponentUser where r.user.id = :userId")
    List<Report> findByUserIdFetchOpponent(@Param("userId") Long userId);

    @Query("select r from Report r where r.user.id = :userId")
    List<Report> findByUserId(@Param("userId") Long userId);
}
