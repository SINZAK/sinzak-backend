package net.sinzak.server.config.auth;

import javax.persistence.Column;

public interface UserProjection { //저장소에서 바로 CustomUserDetails에 주입되지 않아서 거쳐가기.
    @Column(name = "user_id")
    Long getuser_id();
    String getRole();
}
