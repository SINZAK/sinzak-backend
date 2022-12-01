package net.sinzak.server.chatroom.domain;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer participantsNumber;
}
