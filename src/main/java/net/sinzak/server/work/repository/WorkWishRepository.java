package net.sinzak.server.work.repository;

import net.sinzak.server.work.domain.WorkWish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkWishRepository extends JpaRepository<WorkWish, Long> {
}
