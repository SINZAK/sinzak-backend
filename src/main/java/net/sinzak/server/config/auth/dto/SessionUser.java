package net.sinzak.server.config.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.sinzak.server.user.domain.User;

import java.io.Serializable;

@Getter
@AllArgsConstructor
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