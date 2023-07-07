package com.server.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Translation {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "selectedTranslation")
    private List<GroupSettings> groupSettings;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "translations")
    private List<Movie> movies;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Resolution> resolutions = new ArrayList<>();

    public Translation(String name) {
        this.name = name;
    }
}
