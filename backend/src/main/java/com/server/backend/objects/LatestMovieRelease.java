package com.server.backend.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LatestMovieRelease {
    private String movieUrl;
    private String imgUrl;
    private String name;
    private String additionalInfo;
}
