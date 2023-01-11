package net.sinzak.server.chatroom.domain;

import lombok.Getter;
import net.sinzak.server.BaseTimeEntity;

import javax.persistence.*;
import java.util.*;

@Getter
@Entity
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer participantsNumber;
    private String roomId;

    public ChatRoom(){
        this.participantsNumber = 0;
        this.roomId = UUID.randomUUID().toString();
    }

    public ChatRoom(String name) {
        this.name = name;
        this.participantsNumber = 0;
        this.roomId = UUID.randomUUID().toString();
    }

//    @OneToMany(mappedBy = "chatRoom",cascade = CascadeType.ALL)
//    private List<ChatMessage> chatMessages = new ArrayList<>();
//    @OneToMany(mappedBy = "chatRoom",cascade = CascadeType.ALL)
//    private Set<UserChatRoom> userChatRooms = new HashSet<>();

//    public void addUserChatRoom(User user,User inviteUser){
//        UserChatRoom userChatRoom = new UserChatRoom(inviteUser.getName(),inviteUser.getPicture());
//        userChatRoom.setChatRoom(this);
//        userChatRoom.setUser(user);
//        this.participantsNumber++;
//        this.userChatRooms.add(userChatRoom);
//    }

}
