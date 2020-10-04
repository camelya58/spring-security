package com.github.camelya58.jwtauthserver.model;

import com.github.camelya58.jwtauthserver.util.RandomHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс User
 *
 * @author Kamila Meshcheryakova
 * created 01.09.2020
 */
@Data
@SuperBuilder
@AllArgsConstructor
@Entity
@Table(name = "users")
@SuppressWarnings("unused")
public class User implements UserDetails {

    @Id
    private String id;

    private Status status;
    private String username;
    private String email;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinTable(name = "role_users", joinColumns = {
            @JoinColumn(name = "users_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private Set<Role> roles;

    private String refreshToken;
    private long lastVisit;
    private long created;
    private long updated;

    public User() {
        Set<Role> set = new HashSet<>();
        set.add(new Role("ROLE_USER"));
        this.setId(RandomHelper.generate());
        this.status = Status.ACTIVE;
        this.roles = set;
        this.created = new Timestamp(System.currentTimeMillis()).getTime();
        this.updated = new Timestamp(System.currentTimeMillis()).getTime();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        roles.forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });
        return authorities;
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
