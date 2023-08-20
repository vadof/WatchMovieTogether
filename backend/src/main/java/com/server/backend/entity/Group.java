package com.server.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "`group`")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    @ManyToOne
    private User admin;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private GroupSettings groupSettings;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "groups")
    private Set<User> users = new HashSet<>();

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    private Chat chat;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group otherGroup)) return false;
        return Objects.equals(this.id, otherGroup.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
