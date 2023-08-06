package com.server.backend.requests;

import com.server.backend.entity.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeriesSelectionRequest {

    private Long groupId;
    private Series series;
    private SeriesTranslation selectedSeriesTranslation;
    private Season season;
    private Integer episode;

}
