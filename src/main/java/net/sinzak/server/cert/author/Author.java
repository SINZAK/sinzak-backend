package net.sinzak.server.cert.author;

import lombok.Getter;
import net.sinzak.server.BaseTimeEntity;
import net.sinzak.server.cert.Status;

import javax.persistence.*;

@Getter
@Entity
public class Author extends BaseTimeEntity { //인증 작가

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cert_id")
    private Long id;

    @Column
    private String portFolio;  //포트폴리오 링크

    @Enumerated(value = EnumType.STRING)
    private Status status = Status.YET;

    @Column
    private Long userId;

    public Author(String portFolio, Long userId) {
        this.portFolio = portFolio;
        this.userId = userId;
    }

    protected Author() {}

    public void setPortFolio(String portFolio) {
        this.portFolio = portFolio;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
