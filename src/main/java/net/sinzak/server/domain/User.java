package net.sinzak.server.domain;

import lombok.Builder;
import lombok.Getter;
import net.sinzak.server.BaseTimeEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@SequenceGenerator(name = "User_SEQ_GEN",sequenceName = "User_SEQ")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "User_SEQ")
    @Column(name = "user_id")
    private Long id;

    @Column
    private String email;

    @Column
    private String name;

    @Column
    private String nickName;

    @Column
    private String picture;

//    @Column
//    private String introduction;
//
//    @Column
//    private String univ;
//
//    @Column
//    private String major;
//
//    @Column
//    private int followingNum;
//
//    @Column
//    private int followerNum;
//
//    @Column
//    private String stack;
//
//    @Column
//    private String genre;
//
//    @Column
//    private boolean cert_uni;
//
//    @Column
//    private boolean cert_celeb;

    @Column
    private String origin;


    @Enumerated(EnumType.STRING)
    private Role role;


    @Builder
    public User(String name, String email, String picture, String origin, Role role) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.origin = origin;
        this.role = role;
        this.nickName = name;
    }

    protected User() {
    }

    public User update(String name, String picture){
        this.name =name;
        this.picture = picture;
        return this;
    }

    public String getRoleKey(){
        return this.role.getKey();
    }



}