package net.sinzak.server.alarm.repository;

import net.sinzak.server.alarm.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface AlarmRepository extends JpaRepository<Alarm,Long> {

    @Query("select a from Alarm a where a.user.id = :userId")
    Set<Alarm> findByUserId(@Param("userId") Long userId);
}
