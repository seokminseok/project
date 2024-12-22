package com.lsm.declaration.detail;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private final String email;
    private final String nickname; // 닉네임 추가
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(String email, String nickname, String password, Collection<? extends GrantedAuthority> authorities) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.authorities = authorities;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // username은 email로 설정
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
