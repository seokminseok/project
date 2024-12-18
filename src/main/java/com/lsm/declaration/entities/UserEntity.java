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
public class UserEntity implements UserDetails,OAuth2User  {

    @Id
    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "nickname", length = 10)
    private String nickname;

    @Column(name = "contact", length = 12)
    private String contact;

    @Column(name = "create_at")
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_admin")
    private boolean isAdmin;

    @Column(name = "is_suspended")
    private boolean isSuspended;

    @Column(name = "is_verified")
    private boolean isVerified;

    @Column(name = "warning")
    private int warning;



    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    @Override
    public String getUsername() {
        return nickname;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (isAdmin) {
            authorities.add(new SimpleGrantedAuthority("IS_ADMIN")); // ROLE_* 제거
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isSuspended;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isVerified && !isSuspended;
    }

//    @Column(name = "oauth2_provider")
//    private String oauth2Provider; // 예: GOOGLE, FACEBOOK 등
//
//    @Column(name = "oauth2_id")
//    private String oauth2Id;

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
                "is_suspended", this.isSuspended,
                "is_verified", this.isVerified,
                "warning", this.warning
        );
    }

    @Override
    public String getName() {

        return email;

    }
}