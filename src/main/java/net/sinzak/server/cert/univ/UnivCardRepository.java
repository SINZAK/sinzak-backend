package net.sinzak.server.cert.univ;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UnivCardRepository extends JpaRepository<UnivCard, Long> {
    @Query("select c from UnivCard c where c.userId = :userId")
    Optional<UnivCard> findCertByUserId(@Param("userId") Long userId);
}
