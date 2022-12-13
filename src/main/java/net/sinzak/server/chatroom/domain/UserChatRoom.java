package net.sinzak.server.chatroom.domain;


import lombok.Getter;
import net.sinzak.server.user.domain.User;

import javax.persistence.*;

@Getter
@Entity
public class UserChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="CHATROOM_ID")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID")
    private User user;

    private String roomName;
    private String image;
    public UserChatRoom(){}

    public UserChatRoom(String roomName,String image){
        this.roomName = roomName;
        this.image = image;
    }
    public void setUser(User user){
        this.user = user;
    }
    public void setChatRoom(ChatRoom chatRoom){
        this.chatRoom = chatRoom;
    }


}
