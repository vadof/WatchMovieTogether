package com.server.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Series {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String link;

    @NotBlank
    private String name;

    @JsonIgnore
    private int searches = 1;

    @OneToMany(fetch = FetchType.LAZY)
    private List<SeriesTranslation> seriesTranslations = new ArrayList<>();

    public void addSearch() {
        this.searches++;
    }
}
