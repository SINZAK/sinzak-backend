package net.sinzak.server.user.repository;

import net.sinzak.server.user.domain.follow.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow,Long> {


}
