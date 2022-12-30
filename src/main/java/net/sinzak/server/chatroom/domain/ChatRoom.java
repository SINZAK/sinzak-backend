package net.sinzak.server.chatroom.domain;

import lombok.Getter;
import net.sinzak.server.BaseTimeEntity;
import net.sinzak.server.user.domain.User;

import javax.persistence.*;
import java.util.*;

@Getter
@Entity
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer participantsNumber;
    private UUID uuid;

    public ChatRoom(){
        this.participantsNumber = 0;
        this.uuid = UUID.randomUUID();
    }

    @OneToMany(mappedBy = "chatRoom",cascade = CascadeType.ALL)
    private List<ChatMessage> chatMessages = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom",cascade = CascadeType.ALL)
    private Set<UserChatRoom> userChatRooms = new HashSet<>();

    public void addUserChatRoom(User user,User inviteUser){
        UserChatRoom userChatRoom = new UserChatRoom(inviteUser.getName(),inviteUser.getPicture());
        userChatRoom.setChatRoom(this);
        userChatRoom.setUser(user);
        this.participantsNumber++;
        this.userChatRooms.add(userChatRoom);
    }

}
