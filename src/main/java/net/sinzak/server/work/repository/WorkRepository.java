package net.sinzak.server.work.repository;

import net.sinzak.server.work.Work;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkRepository extends JpaRepository<Work, Long> {
}