package net.sinzak.server.chatroom.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.*;

@Getter
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer participantsNumber;


    private UUID uuid;

    @OneToMany(mappedBy = "chatRoom",cascade = CascadeType.ALL)
    private List<ChatMessage> chatMessages = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom",cascade = CascadeType.ALL)
    private Set<UserChatRoom> userChatRooms = new HashSet<>();

}
