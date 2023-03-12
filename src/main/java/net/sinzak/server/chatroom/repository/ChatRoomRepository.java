package net.sinzak.server.chatroom.repository;

import net.sinzak.server.chatroom.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
    @Query("select c from ChatRoom c where c.roomUuid = :roomUuId")
    Optional<ChatRoom> findByRoomId(@Param("roomUuId") String roomUuId);

    @Query("select c from ChatRoom c where c.product.id = :productId")
    List<ChatRoom> findChatRoomByProductId(@Param("productId") Long productId);

    @Query("select c from ChatRoom c where c.work.id = :workId")
    List<ChatRoom> findChatRoomByWorkId(@Param("workId") Long workId);

    @Query("select c from ChatRoom c left join fetch c.chatMessages where c.roomUuid = :roomUuid")
    Optional<ChatRoom> findByRoomUuidFetchChatMessage(@Param("roomUuid") String roomUuid);

    @Query("select c from ChatRoom c left join fetch  c.userChatRooms where c.roomUuid = :roomUuid")
    Optional<ChatRoom> findByRoomUuidFetchUserChatRoom(@Param("roomUuid") String roomUuid);
}
