package com.server.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class GroupSettings {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO Remove if group delete
    @OneToOne
    private MovieSettings movieSettings;

    // TODO Remove if group delete
    @OneToOne
    private SeriesSettings seriesSettings;

    @JsonIgnore
    @OneToOne(mappedBy = "groupSettings")
    private Group group;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<User> usersWithPrivileges = new HashSet<>();
}
