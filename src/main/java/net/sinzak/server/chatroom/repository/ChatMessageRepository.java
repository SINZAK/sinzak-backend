package net.sinzak.server.chatroom.repository;

import net.sinzak.server.chatroom.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {
}
