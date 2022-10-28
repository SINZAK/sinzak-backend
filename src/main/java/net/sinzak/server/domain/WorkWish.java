package net.sinzak.server.domain;

import lombok.Getter;
import net.sinzak.server.BaseTimeEntity;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
public class WorkWish extends BaseTimeEntity {  //다대다 연결 위한 테이블.

    @Id
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

    private void setUser(User user){
        user.getWorkWishList().add(this); //스크랩!
        this.user=user;
    }

    private void setWork(Work work) {  this.work = work; }

    protected WorkWish() {}
}
