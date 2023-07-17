package net.sinzak.server.user.repository;


import net.sinzak.server.config.auth.UserProjection;
import net.sinzak.server.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select user_id, role from user u where user_id= :id and is_delete = false", nativeQuery = true)
    Optional<UserProjection> findCurrentUserInfo(@Param("id") Long id);

    @Query("select u from User u where u.email= :email and u.isDelete = false")
    Optional<User> findByEmailNotDeleted(@Param("email") String email);

    //유저 Id
    @Query("select u from User u where u.id = :id and u.isDelete = false")
    Optional<User> findByIdNotDeleted(@Param("id") Long id);

    @Query("select u from User u where u.isDelete = false")
    List<User> findAllNotDeleted();

    @Query("select u from User u where u.nickName = :nickName and u.isDelete = false")
    Optional<User> findByNickName(@Param("nickName") String nickName);

    //Product
    @Query("select u from User u left join fetch u.productWishes where u.id = :id and u.isDelete = false")
    Optional<User> findByIdFetchProductWishList(@Param("id") Long id);

    @Query("select u from User u left join fetch u.productPosts where u.id = :id and u.isDelete = false")
    Optional<User> findByIdFetchProductPostList(@Param("id") Long id);

    @Query("select u from User u left join fetch u.productSells where u.id = :id and u.isDelete = false")
    Optional<User> findByIdFetchProductSellList(@Param("id") Long id);

    @Query("select u from User u left join fetch u.productLikesList where u.id = :id and u.isDelete = false")
    Optional<User> findByIdFetchLikesList(@Param("id") Long id);


    //Work
    @Query("select u from User u left join fetch u.workPosts where u.id = :id and u.isDelete = false")
    Optional<User> findByIdFetchWorkPostList(@Param("id") Long id);

    @Query("select u from User u left join fetch u.workWishes where u.id = :id and u.isDelete = false")
    Optional<User> findByIdFetchWorkWishList(@Param("id") Long id);



    //Follow

    @Query("select u from User u left join fetch u.followings where u.id =:id and u.isDelete = false")
    Optional<User> findByIdFetchFollowings(@Param("id") Long id);

    @Query("select u from User u left join fetch u.followings fg left join fetch u.productLikesList where u.id = :id and u.isDelete = false")
    Optional<User> findByIdFetchFollowingAndLikesList(@Param("id") Long id);

    @Query("select u from User u left join fetch u.reports where u.id = :id and u.isDelete = false")
    Optional<User> findByIdFetchReportList(@Param("id") Long id);

    @Query("select u from User u left join fetch u.productLikesList left join fetch u.histories where u.id = :id and u.isDelete = false")
    Optional<User> findByIdFetchHistoryAndLikesList(@Param("id") Long id);


}
