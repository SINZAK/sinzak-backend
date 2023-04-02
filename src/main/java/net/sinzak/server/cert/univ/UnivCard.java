package net.sinzak.server.cert.univ;

import lombok.Getter;
import net.sinzak.server.BaseTimeEntity;
import net.sinzak.server.cert.Status;

import javax.persistence.*;

@Getter
@Entity
public class UnivCard extends BaseTimeEntity { //대학 학생증인증.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cert_id")
    private Long id;

    @Column
    private String univName;

    @Column
    private String univCardUrl;  //학생증 url

    @Enumerated(value = EnumType.STRING)
    private Status status = Status.YET;

    @Column
    private Long userId;

    public UnivCard(String univName, String univCardUrl, Long userId) {
        this.univName = univName;
        this.univCardUrl = univCardUrl;
        this.userId = userId;
    }

    protected UnivCard() {}

    public void updateImageUrl(String imageUrl) {
        this.univCardUrl = imageUrl;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
