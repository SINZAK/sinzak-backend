package net.sinzak.server.config.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class CustomUserDetails implements UserDetails, Serializable {
    private String role;
    private String username;
    private Collection<? extends GrantedAuthority> authorities = null;
    /** authorities는 역직렬화가 안됨. 굳이 하려면 엄청 복잡하게 해야함. 따라서 레디스에 저장은 null로 하고, 불러온 뒤에 권한 세팅 하는 방법으로 고정. **/
    public String getRole() {
        return role;
    }

    public void setAuthorities() {
        List<SimpleGrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority(this.role));
        this.authorities = list;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {  /** email 사용 !!! **/
        return username;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    @Builder
    public CustomUserDetails(Long id, String role) {
        super();
        this.role = "ROLE_"+role;
        this.username = String.valueOf(id);
        this.authorities = getAuthorities();
    }

    public CustomUserDetails() {}

}
