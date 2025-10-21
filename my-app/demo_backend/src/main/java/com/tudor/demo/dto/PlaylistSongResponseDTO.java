package com.tudor.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistSongResponseDTO {
    private Integer songId;
    private String songName;
    private Integer albumId;
    private String albumName;
    private String albumImagePath;
}

