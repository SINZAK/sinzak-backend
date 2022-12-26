package net.sinzak.server.config.auth.jwt;

import lombok.Builder;
import lombok.Getter;
import net.sinzak.server.BaseTimeEntity;

import javax.persistence.*;

@Entity
@Table(name = "refresh_token")
@Getter
public class RefreshToken extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "refresh_key")
    private Long key;

    @Column
    private String token;

    protected RefreshToken() {}

    public RefreshToken updateToken(String token) {
        this.token = token;
        return this;
    }

    @Builder
    public RefreshToken(Long key, String token) {
        this.key = key;
        this.token = token;
    }
}