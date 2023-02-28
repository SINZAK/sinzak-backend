package net.sinzak.server.user.repository;

import net.sinzak.server.user.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("select r from Report  r where r.opponentUser.id = :opponentUserId and r.user.id = :userId")
    Optional<Report> findByUserIdAndOpponentUserId(@Param("userId") Long userId,@Param("opponentUserId") Long opponentUserId);
}
