package net.sinzak.server.chatroom.domain;


import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.*;
import net.sinzak.server.BaseTimeEntity;

import javax.persistence.*;
import java.util.UUID;


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
    private String sender;
    private String receiver;
    private String roomId;

    @Setter
    @Lob private String message; //모든 언어에 다 있는 byte[]


    public void setSender(String sender){
        this.sender =sender;
    }

    public void newConnect(){
        this.type = MessageType.ENTER;
    }

    public void closeConnect(){
        this.type = MessageType.LEAVE;
    }
}
