package net.sinzak.server.work.domain;

import lombok.Getter;
import net.sinzak.server.user.domain.User;

import javax.persistence.*;

@Entity
@Getter
public class WorkSuggest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "suggest_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER) // (fetch = FetchType.LAZY)  반드시 work의 정보는 같이 불러와야하기에
    @JoinColumn(name = "work_id")
    private Work work;

    public static WorkSuggest createConnect(Work work, User user){  //생성메서드
        return new WorkSuggest(user,work);
    }


    public void setWork(Work work) {  this.work = work; }

    protected WorkSuggest() {}

    public WorkSuggest(User user, Work work) {
        this.user = user;
        this.work = work;
    }
}
