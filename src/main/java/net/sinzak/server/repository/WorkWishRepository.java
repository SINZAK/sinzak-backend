package net.sinzak.server.repository;

import net.sinzak.server.domain.WorkWish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkWishRepository extends JpaRepository<WorkWish, Long> {
}
