package net.sinzak.server.user.repository;

import net.sinzak.server.user.domain.JoinTerms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinTermsRepository extends JpaRepository<JoinTerms, Long> {
}
