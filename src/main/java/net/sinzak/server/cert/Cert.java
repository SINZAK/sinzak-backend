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
    private String univ_email;  //대학 메일

    @Column
    private String code;  //메일 인증번호

    @Column
    private boolean verified;  //메일 인증번호

    public Cert(String univ_email, String code) {
        this.univ_email = univ_email;
        this.code = code;
    }

    protected Cert() {
    }

    public void updateKey(String code) {
        this.code = code;
    }

    public void setVerified() {
        this.verified = true;
    }
}
