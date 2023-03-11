package net.sinzak.server.work.domain;

import lombok.Getter;
import net.sinzak.server.user.domain.User;

import javax.persistence.*;

@Entity
@Getter
@SequenceGenerator(name = "Work_SELL_SEQ_GEN",sequenceName = "Work_SELL_SEQ")
public class WorkSell {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Work_SELL_SEQ")
    @Column(name = "sell_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER) // (fetch = FetchType.LAZY)  반드시 product의 정보는 같이 불러와야하기에
    @JoinColumn(name = "work_id")
    private Work work;

    public static WorkSell createConnect(Work work, User user){  //생성메서드
        WorkSell connect = new WorkSell();
        connect.setWork(work);
        connect.setUser(user);
        return connect;
    }

    private void setUser(User user){
        user.getWorkSellList().add(this); //구매목록에 추가
        this.user=user;
    }

    public void setWork(Work work) {  this.work = work; }

    protected WorkSell() {}
}
