package net.sinzak.server.user.repository;


import net.sinzak.server.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(@Param("email")String email);

    @Query("select u from User u left join fetch u.workPostList where u.email = :email")
    Optional<User> findByEmailFetchWP(@Param("email")String email);   /** 유저가 쓴 외주 글까지 불러오기 **/

    @Query("select u from User u left join fetch u.productPostList where u.email = :email")
    Optional<User> findByEmailFetchPP(@Param("email")String email);   /** 유저가 쓴 작품 글까지 불러오기 **/


    @Query("select u from User u left join fetch u.workWishList where u.email = :email")
    Optional<User> findByEmailFetchWW(@Param("email")String email);   /** 유저가 누른 외주 찜 목록까지 불러오기 **/

    @Query("select u from User u left join fetch u.productWishList where u.email = :email")
    Optional<User> findByEmailFetchPW(@Param("email")String email);   /** 유저가 누른 작품 찜 목록까지 불러오기 **/

    @Query("select u from User u left join fetch u.likesList where u.email = :email")
    Optional<User> findByEmailFetchLL(@Param("email")String email);   /** 유저가 누른 작품 좋아요목록까지 불러오기 **/

    @Query("select u from User u left join fetch u.productSellList where u.email = :email")
    Optional<User> findByEmailFetchPS(@Param("email")String email);   /** 유저가 판매 한 작품 목록까지 불러오기 **/

    @Query("select u from User u left join fetch u.followingList where u.email = :email")
    Optional<User> findByEmailFetchFL(@Param("email")String email);   /** 유저의 팔로잉 목록까지 불러오기 **/

    @Query("select u from User u left join fetch u.followingList left join fetch u.likesList where u.email = :email")
    Optional<User> findByEmailFetchFLandLL(@Param("email")String email);   /** 유저의 팔로잉 목록, 좋아요 목록까지 불러오기 **/

}
