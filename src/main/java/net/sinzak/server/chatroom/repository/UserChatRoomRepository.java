package net.sinzak.server.chatroom.repository;

import net.sinzak.server.chatroom.domain.UserChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserChatRoomRepository extends JpaRepository<UserChatRoom,Long> {

    @Query("select uc from UserChatRoom as uc where uc.user.email =:sessionUserEmail")
    List<UserChatRoom> findUserChatRoomBySessionUserEmail(@Param ("sessionUserEmail") String sessionUserEmail);
}
