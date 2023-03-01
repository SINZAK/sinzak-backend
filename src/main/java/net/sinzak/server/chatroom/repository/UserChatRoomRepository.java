package net.sinzak.server.chatroom.repository;

import net.sinzak.server.chatroom.domain.UserChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserChatRoomRepository extends JpaRepository<UserChatRoom,Long> {

    @Query("select uc from UserChatRoom as uc where uc.user.email = :email")
    List<UserChatRoom> findUserChatRoomByEmail(@Param ("email") String email);


    @Query("select uc from UserChatRoom uc left join fetch uc.chatRoom where uc.user.email = :email")
    List<UserChatRoom> findUserChatRoomByEmailFetchChatRoom(@Param("email") String email);

    @Query("select uc from UserChatRoom uc left join fetch uc.chatRoom where uc.user.id = :id")
    List<UserChatRoom> findUserChatRoomByIdFetchChatRoom(@Param("id") Long id);

    @Query("select uc from UserChatRoom uc left join fetch uc.chatRoom where uc.user.id = :id and uc.isDisable = false")
    List<UserChatRoom> findUserChatRoomByIdFetchChatRoomWhereNotDisabled(@Param("id") Long id);

}
