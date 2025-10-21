package com.tudor.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ArtistInfluencesDTO {
    @NotNull(message = "Artist ID is required")
    private Integer artistId;

    @NotNull(message = "Influenced by ID is required")
    private Integer influencedById;
}
