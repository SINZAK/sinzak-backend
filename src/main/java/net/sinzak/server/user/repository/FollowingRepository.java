package net.sinzak.server.user.repository;

import net.sinzak.server.user.domain.follow.Following;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface FollowingRepository extends JpaRepository<Following,Long> {

    @Query("select fg,fu.id,fu.nickName,fu.picture from Following fg " +
            "left join fetch fg.followingUser fu " +
            "where fg.user.id = :userId and fg.followingUser.isDelete = false")
    Set<Following> findFollowingByUserIdFetchFollowingUserIdAndNickNameAndPicture(@Param("userId") Long userId);

    @Query("select fg from Following fg where fg.user.id = :userId and fg.followingUser.id = :followingId")
    Optional<Following> findFollowingByUserIdAndFollowingUserId(@Param("userId") Long userId, @Param("followingId") Long followingId);
}
