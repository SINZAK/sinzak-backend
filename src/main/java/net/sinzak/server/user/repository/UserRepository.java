package net.sinzak.server.user.repository;


import net.sinzak.server.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**유저 이메일로 구분 및 검색 **/
    @Query("select u from User u where u.email= :email")
    Optional<User> findByEmail(@Param("email")String email);

    @Query("select u from User u where u.email= :email and u.isDelete = false")
    Optional<User> findByEmailNotDeleted(@Param("email")String email);

    //유저 Id
    @Query("select u from User u where u.id = :id and u.isDelete = false")
    Optional<User> findByIdNotDeleted(@Param("id")Long id);


    @Query("select u from User u where u.nickName = :nickName and u.isDelete = false")
    Optional<User> findByNickName(@Param("nickName")String nickName);

    //Product
    @Query("select u from User u left join fetch u.productWishList where u.id = :id and u.isDelete = false")
    Optional<User> findByIdFetchProductWishList(@Param("id") Long id);

    @Query("select u from User u left join fetch u.productPostList where u.id = :id and u.isDelete = false")
    Optional<User> findByIdFetchProductPostList(@Param("id")Long id);

    @Query("select u from User u left join fetch u.productSellList where u.id = :id and u.isDelete = false")
    Optional<User> findByIdFetchProductSellList(@Param("id")Long id);

    @Query("select u from User u left join fetch u.productLikesList where u.id = :id and u.isDelete = false")
    Optional<User> findByIdFetchLikesList(@Param("id")Long id);

    //Work

    @Query("select u from User u left join fetch u.workPostList where u.id = :id and u.isDelete = false")
    Optional<User> findByIdFetchWorkPostList(@Param("id")Long id);

    @Query("select u from User u left join fetch u.workWishList where u.id = :id and u.isDelete = false")
    Optional<User> findByIdFetchWorkWishList(@Param("id")Long id);

    //Product and Work
    @Query("SELECT u FROM User u JOIN FETCH u.productPostList JOIN FETCH u.workPostList WHERE u.id = :id and u.isDelete = false")
    Optional<User> findByIdFetchWorkListAndProductList(@Param("id") Long id); //두개 조인할 때는 left 쓰면 안 됨 left join 두 번을 쓰거나 Left join fetch

    //Follow
    @Query("select u from User u left join fetch u.followerList where u.id =:id and u.isDelete = false")
    Optional<User> findByIdFetchFollowerList(@Param("id") Long id);

    @Query("select u from User u left join fetch u.followingList where u.id =:id and u.isDelete = false")
    Optional<User> findByIdFetchFollowingList(@Param("id") Long id);

    @Query("select u from User u left join fetch u.followingList left join fetch u.productLikesList where u.id = :id and u.isDelete = false")
    Optional<User> findByIdFetchFollowingAndLikesList(@Param("id") Long id);

    @Query("select u from User u left join fetch u.reportList where u.id = :id and u.isDelete = false")
    Optional<User> findByIdFetchReportList(@Param("id")Long id);

    @Query("select u from User u left join fetch u.productLikesList left join fetch u.historyList where u.id = :id and u.isDelete = false")
    Optional<User> findByIdFetchHistoryAndLikesList(@Param("id")Long id);
}
