package com.lsm.declaration.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Entity
@Table(schema = "fave", name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode(of = {"email"})
@Setter
@ToString
public class UserEntity implements UserDetails, OAuth2User {

    @Getter
    @Id
    @Column(name = "email", length = 50, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "nickname", length = 10, nullable = false)
    private String nickname;

    @Column(name = "contact", length = 12)
    private String contact;

    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;

    @Column(name = "is_suspended", nullable = false)
    private boolean isSuspended;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;

    @Column(name = "warning", nullable = false)
    private int warning;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // UserDetails 메서드 구현
    @Override
    public String getUsername() {
        return email; // 기본적으로 이메일을 username으로 사용
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (isAdmin) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN")); // ROLE_ADMIN
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER")); // 일반 사용자
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isSuspended; // 계정 잠금 여부
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명 만료 여부
    }

    @Override
    public boolean isEnabled() {
        return isVerified && !isSuspended; // 계정 활성화 여부
    }

    // OAuth2User 메서드 구현
    @Override
    public Map<String, Object> getAttributes() {
        return Map.of(
                "email", this.email,
                "nickname", this.nickname,
                "contact", this.contact,
                "createAt", this.createdAt,
                "updateAt", this.updatedAt,
                "deletedAt", this.deletedAt,
                "isAdmin", this.isAdmin,
                "isSuspended", this.isSuspended,
                "isVerified", this.isVerified,
                "warning", this.warning
        );
    }

    @Override
    public String getName() {
        return email;
    }


}
