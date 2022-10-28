package net.sinzak.server.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Getter
@Entity
@SequenceGenerator(name = "Work_SEQ_GEN",sequenceName = "Work_SEQ")
public class Work { /** 외주 **/


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Work_SEQ")
    @Column(name = "work_id")
    private Long id;  //작품 번호

    @Column
    private String title;
    @Column
    private String name; //아마 닉네임이 들어갈 예정
    @Column
    private String univ;

    @Column
    private int views;
    @Column
    private int wishCnt;
    @Column
    private int price;
    @Column
    private boolean complete;

    @OneToMany(mappedBy = "work", cascade = CascadeType.REMOVE)
    private List<WishWork> wishWorkList = new ArrayList<>();  //프로젝트-회원 엮여있는 리스트  스크랩!!!!
}

