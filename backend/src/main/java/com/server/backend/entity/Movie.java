package com.server.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Movie {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String link;

    @NotBlank
    private String name;

    @JsonIgnore
    private int searches = 1;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Translation> translations = new ArrayList<>();

    @OneToMany(mappedBy = "selectedMovie")
    private List<GroupSettings> groupSettings = new ArrayList<>();

    public void addSearch() {
        this.searches++;
    }
}
