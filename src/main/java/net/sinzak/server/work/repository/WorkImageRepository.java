package net.sinzak.server.work.repository;

import net.sinzak.server.work.domain.WorkImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkImageRepository extends JpaRepository<WorkImage,Long> {
}
