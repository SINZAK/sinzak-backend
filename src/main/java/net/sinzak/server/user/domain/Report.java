package net.sinzak.server.user.domain;

import lombok.Getter;
import net.sinzak.server.BaseTimeEntity;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@Entity
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "report_id")
    private Long id;  //작품 번호

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="opponent_user_id")
    private User opponentUser;

    @Lob
    private String reason;


    public static Report createConnect(User loginUser, User opponentUser){  //생성메서드
        Report connect = new Report();
        connect.setUser(loginUser, opponentUser);
        return connect;
    }

    public void setUser(User loginUser, User opponentUser) {
        this.user = loginUser;
        user.getReportList().add(this);
        this.opponentUser = opponentUser;
    }
}
