package net.sinzak.server.work.repository;

import net.sinzak.server.work.domain.WorkImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkImageRepository extends JpaRepository<WorkImage, Long> {
    @Query("select i from WorkImage i where i.work.id = :id")
    List<WorkImage> findByWorkId(@Param("id") Long id);
}
