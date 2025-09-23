package com.catalogservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 32, unique = true)
    private String name;

    public Role(String name) {
        if(Objects.isNull(name) || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name is null or empty");
        } else if(!name.startsWith("ROLE_")) {
            throw new IllegalArgumentException("Role name must start with 'ROLE_'");
        } else  {
            this.name = name;
        }
    }

    public void setName(String name) {
        if(Objects.isNull(name) || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name is null or empty");
        }  else if(!name.startsWith("ROLE_")) {
            throw new IllegalArgumentException("Role name must start with 'ROLE_'");
        }  else  {
            this.name = name;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role other)) return false;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }
}
