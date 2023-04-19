package net.sinzak.server.user.repository;

import net.sinzak.server.user.domain.follow.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface FollowRepository extends JpaRepository<Follow,Long> {
    @Query("select f  from Follow f where f.followingUser.id = :followingUserId and f.followerUser.id = :followerUserId")
    Optional<Follow> findFollowingsByFollowerUserAndFollowingUser(@Param("followingUserId")Long followingId, @Param("followerUserId")Long followerId);

    @Query("select f,fg.id,fg.nickName,fg.picture from Follow f left join fetch f.followingUser fg where f.followerUser.id = :loginUserId")
    Set<Follow> findByFollowerUserIdFetchFollowings(@Param("loginUserId") Long loginUserId);
    @Query("select f,fr.id,fr.nickName,fr.picture from Follow f left join fetch f.followerUser fr where f.followingUser.id = :loginUserId")
    Set<Follow> findByFollowingUserIdFetchFollower(@Param("loginUserId") Long loginUserId);


}
