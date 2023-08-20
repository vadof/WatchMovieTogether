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

    @OneToOne(cascade = CascadeType.ALL)
    private MovieSettings movieSettings;

    @OneToOne(cascade = CascadeType.ALL)
    private SeriesSettings seriesSettings;

    @JsonIgnore
    @OneToOne(mappedBy = "groupSettings")
    private Group group;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<User> usersWithPrivileges = new HashSet<>();
}
