package net.sinzak.server.chatroom.domain;


import com.fasterxml.jackson.databind.ser.Serializers;
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
    private String id;

    private String type;
    private String sender;
    private String receiver;

    @Lob private String message; //모든 언어에 다 있는 byte[]

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name ="CHATROOM_ID")
    private ChatRoom chatRoom;



    public void setSender(String sender){
        this.sender =sender;
    }

    public void newConnect(){
        this.type = "new";
    }

    public void closeConnect(){
        this.type = "close";
    }
}
