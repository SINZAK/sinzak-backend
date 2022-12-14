package net.sinzak.server.user.repository;


import net.sinzak.server.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(@Param("email")String email);

    @Query("select u from User u left join fetch u.productPostList where u.email = :email")
    Optional<User> findByEmailFetchProductPostList(@Param("email")String email);

    @Query("select u from User u left join fetch u.workPostList where u.email = :email")
    Optional<User> findByEmailFetchWorkPostList(@Param("email")String email);

    @Query("select u from User u left join fetch u.productWishList where u.email = :email")
    Optional<User> findByEmailFetchProductWishList(@Param("email")String email);

    @Query("select u from User u left join fetch u.workWishList where u.email = :email")
    Optional<User> findByEmailFetchWorkWishList(@Param("email")String email);


    @Query("select u from User u left join fetch u.productLikesList where u.email = :email")
    Optional<User> findByEmailFetchLikesList(@Param("email")String email);

    @Query("select u from User u left join fetch u.productSellList where u.email = :email")
    Optional<User> findByEmailFetchProductSellList(@Param("email")String email);

    @Query("select u from User u left join fetch u.followingList where u.email = :email")
    Optional<User> findByEmailFetchFollowingList(@Param("email")String email);

    @Query("select u from User u left join fetch u.followingList left join fetch u.productLikesList where u.email = :email")
    Optional<User> findByEmailFetchFollowingAndLikesList(@Param("email")String email);

    @Query("select u from User u left join fetch u.followerList where u.id =:id")
    Optional<User> findByIdFetchFollowerList(@Param("id") Long id);

}
