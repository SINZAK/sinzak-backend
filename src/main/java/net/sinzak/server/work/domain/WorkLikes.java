package net.sinzak.server.work.domain;

import lombok.Getter;
import net.sinzak.server.user.domain.User;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@Entity
public class WorkLikes {

    @Id
    @GeneratedValue
    @Column(name = "likes_id")
    private Long id;  //작품 번호

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="work_id")
    private Work work;


    public static WorkLikes createConnect(Work work, User user){  //생성메서드
        WorkLikes connect = new WorkLikes();
        connect.setWork(work);
        connect.setUser(user);
        return connect;
    }

    private void setUser(User user){
        user.getWorkLikesList().add(this); //스크랩!
        this.user=user;
    }
    public void setWork(Work work) {
        this.work = work;
    }
}
