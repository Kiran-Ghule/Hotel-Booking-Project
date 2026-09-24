package com.airbnb.project.entities;

import com.airbnb.project.entities.enums.Roles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@Table(name = "app_user")
@AllArgsConstructor
@Slf4j
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Roles> role;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.stream()
                .map(role1 -> new SimpleGrantedAuthority("ROLE_"+role1.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof User user) return  Objects.equals(id, user.getId());
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
