package net.sinzak.server.config;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class UserDetailDto implements Serializable {
    private Long user_id;
    private String role;

    public UserDetailDto(Long user_id, String role) {
        this.user_id = user_id;
        this.role = role;
    }
}
