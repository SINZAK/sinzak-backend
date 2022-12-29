package net.sinzak.server.user.domain;

import lombok.Getter;
import net.sinzak.server.BaseTimeEntity;

import javax.persistence.*;

@Entity
@Getter
public class JoinTerms extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column
    private boolean term1 = true;

    @Column
    private boolean term2 = true;

    @Column
    private boolean term3;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public JoinTerms(boolean term4) {
        this.term3 = term3;
    }

    protected JoinTerms() {}

    public void setUser(User user) {
        this.user = user;
    }
}
