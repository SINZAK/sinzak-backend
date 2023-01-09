package net.sinzak.server.work.repository;

import net.sinzak.server.work.domain.WorkLikes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkLikesRepository extends JpaRepository<WorkLikes, Long> {
}
