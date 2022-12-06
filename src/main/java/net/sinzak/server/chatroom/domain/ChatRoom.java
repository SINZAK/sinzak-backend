package net.sinzak.server.chatroom.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer participantsNumber;

    @OneToMany(mappedBy = "chatRoom",cascade = CascadeType.ALL)
    private List<ChatMessage> chatMessages = new ArrayList<>();




}
