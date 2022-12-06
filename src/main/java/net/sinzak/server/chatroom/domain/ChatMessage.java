package net.sinzak.server.chatroom.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private String sender;
    private String receiver;
    private Object data;

    @Lob private byte[] message; //모든 언어에 다 있는 byte[]

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
