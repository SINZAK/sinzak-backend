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

    private String opponentUserEmail;
    private String opponentUserUniv;
    private String roomName;
    private String image;
    @Lob private byte[] latestMessage;
    public UserChatRoom(){}

    public UserChatRoom(User user,User opponentUser){
        this.roomName = opponentUser.getName();
        this.opponentUserEmail = opponentUser.getEmail();
        this.opponentUserUniv = opponentUser.getUniv();
        this.image = opponentUser.getPicture();
        this.user = user;
    }
    public void setUser(User user){
        this.user = user;
    }
    public void setChatRoom(ChatRoom chatRoom){
        this.chatRoom = chatRoom;
    }


}
