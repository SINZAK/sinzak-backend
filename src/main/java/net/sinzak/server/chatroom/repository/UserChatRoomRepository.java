package net.sinzak.server.chatroom.repository;

import net.sinzak.server.chatroom.domain.UserChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserChatRoomRepository extends JpaRepository<UserChatRoom,Long> {
}
