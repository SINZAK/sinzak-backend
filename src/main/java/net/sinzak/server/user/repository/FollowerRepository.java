package net.sinzak.server.user.repository;

import net.sinzak.server.user.domain.follow.Follower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface FollowerRepository extends JpaRepository<Follower,Long> {
    ////Follow 관련
    @Query("select fr,fu.id,fu.nickName,fu.picture from Follower fr " +
            "left join fetch fr.followerUser fu " +
            "where fr.user.id = :userId and fr.followerUser.isDelete = false")
    Set<Follower> findFollowerByUserIdFetchFollowerUserIdAndNickNameAndPicture(@Param("userId") Long userId);
}
