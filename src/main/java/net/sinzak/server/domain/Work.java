package net.sinzak.server.domain;

import lombok.Builder;
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
    private String content;

    @Column
    private String userName; //닉네임

    @Column
    private int pay;

    @Column
    private boolean suggest;

    @Column
    private String univ="";

    @Column
    private String field;

    @Column
    private int views = 2;

    @Column
    private int wishCnt = 0;

    @Column
    private int chatCnt = 0;

    @Column
    private String photo;

    @Column
    private boolean employment; //고용글인지 피고용글인지

    @Column
    private boolean complete = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  //수취인

    @Builder
    public Work(String title, String content, int pay, boolean suggest, String userName, String univ, String field, String photo, boolean employment) {
        this.title = title;
        this.content = content;
        this.pay = pay;
        this.suggest = suggest;
        this.userName = userName;
        this.univ = univ;
        this.field = field;
        this.photo = photo;
        this.employment = employment;
    }

    public void setUser(User user) {
        user.getWorkPostList().add(this);
        this.user = user;
    }

    @OneToMany(mappedBy = "work", cascade = CascadeType.REMOVE)
    private List<WorkWish> workWishList = new ArrayList<>();  //프로젝트-회원 엮여있는 리스트  스크랩!!!!

    protected Work() {
    }
}

