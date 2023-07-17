package net.sinzak.server.user.repository;

import net.sinzak.server.user.domain.SearchHistory;
import net.sinzak.server.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    @Query("select u from User u left join fetch u.histories where u.id = :id")
    Optional<User> findByIdFetchHistoryList(@Param("id") Long id);
}
