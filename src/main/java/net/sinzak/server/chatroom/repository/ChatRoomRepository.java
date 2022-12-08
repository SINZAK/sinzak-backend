package net.sinzak.server.chatroom.repository;

import net.sinzak.server.chatroom.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
}
