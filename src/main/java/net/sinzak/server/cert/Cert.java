package net.sinzak.server.cert;

import lombok.Getter;
import net.sinzak.server.BaseTimeEntity;

import javax.persistence.*;

@Getter
@Entity
public class Cert extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cert_id")
    private Long id;

    @Column
    private String email;

    @Column
    private String univName;

    @Column
    private String univCardUrl;  //학생증 url

    @Column
    private boolean verified = false;

    public Cert(String email, String univName, String univCardUrl, boolean verified) {
        this.email = email;
        this.univName = univName;
        this.univCardUrl = univCardUrl;
        this.verified = verified;
    }

    protected Cert() {}

    public void updateImageUrl(String imageUrl) {
        this.univCardUrl = imageUrl;
    }

    public void setVerified() {
        this.verified = true;
    }
}
