package net.sinzak.server.work.domain;

import lombok.Getter;
import net.sinzak.server.BaseTimeEntity;
import net.sinzak.server.user.domain.User;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
public class WorkWish extends BaseTimeEntity {  //다대다 연결 위한 테이블.

    @Id
    @GeneratedValue
    @Column(name = "work_wish_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="work_id")
    private Work work;


    public WorkWish(User user, Work work) {
        setUser(user);
        setWork(work);
    }

    public static WorkWish createConnect(Work work, User user){  //생성메서드
        WorkWish connect = new WorkWish();
        connect.setWork(work);
        connect.setUser(user);
        return connect;
    }

    private void setUser(User user){
        user.getWorkWishList().add(this); //스크랩!
        this.user=user;
    }

    private void setWork(Work work) {  this.work = work; }

    protected WorkWish() {}
}
