package net.sinzak.server.cert.author;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CelebRepository extends JpaRepository<Celeb, Long> {

    @Query("select c from Celeb c where c.userId = :userId")
    Optional<Celeb> findCertByUserId(@Param("userId")Long userId);
}
