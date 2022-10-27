package net.sinzak.server.config.auth.dto;

import lombok.Builder;
import lombok.Getter;
import net.sinzak.server.domain.User;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    private String name;
    private String email;
    private String picture;

    @Builder()
    public SessionUser(User user){
        this.name = user.getName();
        this.email = user.getEmail();
        this.picture = user.getPicture();
    }
}