package net.sinzak.server.user.repository;


import net.sinzak.server.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(@Param("email")String email);

    @Query("select u from User u left join fetch u.workPostList where u.email = :email")
    Optional<User> findByEmailFetchWP(@Param("email")String email);

    @Query("select u from User u left join fetch u.productPostList where u.email = :email")
    Optional<User> findByEmailFetchPP(@Param("email")String email);


    @Query("select u from User u left join fetch u.workWishList where u.email = :email")
    Optional<User> findByEmailFetchWW(@Param("email")String email);

    @Query("select u from User u left join fetch u.productWishList where u.email = :email")
    Optional<User> findByEmailFetchPW(@Param("email")String email);

}
