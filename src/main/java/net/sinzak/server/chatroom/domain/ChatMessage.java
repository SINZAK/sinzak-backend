package net.sinzak.server.chatroom.domain;


import lombok.*;
import net.sinzak.server.BaseTimeEntity;

import javax.persistence.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChatMessage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MessageType type;
    private String senderName;
    private Long senderId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;


    @Setter
    @Lob
    private String message; //모든 언어에 다 있는 byte[]


    public void setSenderName(String sender) {
        this.senderName = sender;
    }

    public void newConnect() {
        this.type = MessageType.ENTER;
    }

    public void closeConnect() {
        this.type = MessageType.LEAVE;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}
