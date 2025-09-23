package com.catalogservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users", indexes = {@Index(columnList = "username"), @Index(columnList = "email")})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 254)
    private String email;
    @Column(nullable = false, length = 32)
    private String username;
    @Column(nullable = false, length = 100)
    private String passwordHash;
    @Column(nullable = false, name = "enabled")
    private Boolean isEnabled;
    @Column(nullable= false)
    private Instant createdAt;
    @Column(nullable= false)
    private Instant updatedAt;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"
            ), inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles = new HashSet<>();

    public User(String email, String username, String passwordHash, Boolean isEnabled) {
        if(Objects.isNull(email) || email.trim().isEmpty()) {
            throw new IllegalArgumentException("User email is null or empty");
        } else {
            this.email = email.trim().toLowerCase();
        }

        if(Objects.isNull(username) || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is null or empty");
        } else {
            this.username = username.trim();
        }

        if(Objects.isNull(passwordHash) || passwordHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Password hash is null or empty");
        } else  {
            this.passwordHash = passwordHash.trim();
        }
        this.isEnabled = isEnabled;
    }

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    public void setEmail(String email) {
        if(Objects.isNull(email) || email.trim().isEmpty()) {
            throw new IllegalArgumentException("User email is null or empty");
        } else {
            this.email = email.trim().toLowerCase();
        }
    }

    public void setUsername(String username) {
        if(Objects.isNull(username) || username.trim().isEmpty()) {
            throw new IllegalArgumentException("username is null or empty");
        } else {
            this.username = username.trim();
        }
    }

    public void setPasswordHash(String passwordHash) {
        if(Objects.isNull(passwordHash) || passwordHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Password hash is null or empty");
        } else  {
            this.passwordHash = passwordHash.trim();
        }
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;

        if (this.id != null && other.id != null) {
            return Objects.equals(this.id, other.id);
        }

        return Objects.equals(this.email, other.email);
    }

    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : Objects.hash(email);
    }
}
