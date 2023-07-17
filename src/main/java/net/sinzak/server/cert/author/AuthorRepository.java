package net.sinzak.server.cert.author;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("select a from Author a where a.userId = :userId")
    Optional<Author> findCertByUserId(@Param("userId") Long userId);
}
