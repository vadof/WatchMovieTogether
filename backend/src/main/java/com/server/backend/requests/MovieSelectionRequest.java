package com.server.backend.requests;

import com.server.backend.entity.Movie;
import com.server.backend.entity.Resolution;
import com.server.backend.entity.Translation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieSelectionRequest {

    private Long groupId;
    private Movie movie;
    private Translation selectedTranslation;

}
