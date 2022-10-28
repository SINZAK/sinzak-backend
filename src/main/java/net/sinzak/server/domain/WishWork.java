package net.sinzak.server.domain;

import lombok.Getter;
import net.sinzak.server.BaseTimeEntity;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
public class WishWork extends BaseTimeEntity {  //다대다 연결 위한 테이블.

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "Project_SEQ")
    @Column(name = "wish_work_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="work_id")
    private Work work;


    public WishWork(User user, Work work) {
        setUser(user);
        setWork(work);
    }

    private void setUser(User user){
        user.getWishWorkList().add(this); //스크랩!
        this.user=user;
    }

    private void setWork(Work work) {  this.work = work; }

    protected WishWork() {}
}
