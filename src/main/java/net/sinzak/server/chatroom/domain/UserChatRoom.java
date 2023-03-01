package net.sinzak.server.chatroom.domain;


import lombok.Getter;
import net.sinzak.server.user.domain.User;

import javax.persistence.*;
import java.time.LocalDateTime;

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


    private Long opponentUserId;
    private String opponentUserUniv;
    private String roomName;
    private String image;
    private String latestMessage;
    private LocalDateTime latestMessageTime;
    public UserChatRoom(){}

    public void updateLatestMessage(String latestMessage){
        this.latestMessage = latestMessage;
        this.latestMessageTime = LocalDateTime.now();
    }
    public UserChatRoom(User user,User opponentUser){
        this.latestMessage = null;
        this.latestMessageTime = null;
        this.roomName = opponentUser.getNickName();
        this.opponentUserId = opponentUser.getId();
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
