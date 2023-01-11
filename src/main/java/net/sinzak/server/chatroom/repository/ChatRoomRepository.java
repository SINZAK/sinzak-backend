package net.sinzak.server.chatroom.repository;

import net.sinzak.server.chatroom.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
    @Query("select c from ChatRoom c where c.roomId = :roomId")
    Optional<ChatRoom> findByRoomId(@Param("roomId") String roomId);
}
