package net.sinzak.server.chatroom.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Lob;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private String type;
    private String sender;
    private String receiver;
    private Object data;

    @Lob private byte[] message; //모든 언어에 다 있는 byte[]



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
