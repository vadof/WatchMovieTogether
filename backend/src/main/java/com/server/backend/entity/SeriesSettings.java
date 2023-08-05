package com.server.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeriesSettings {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Series selectedSeries;

    @ManyToOne
    private SeriesTranslation selectedTranslation;

    @ManyToOne
    private Season selectedSeason;

    private int selectedEpisode;

    @JsonIgnore
    @OneToOne(mappedBy = "seriesSettings")
    private GroupSettings groupSettings;
}
