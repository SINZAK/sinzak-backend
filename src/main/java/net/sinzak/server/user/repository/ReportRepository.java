package net.sinzak.server.user.repository;

import net.sinzak.server.user.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
