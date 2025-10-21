package com.tudor.demo.dto;

import com.tudor.demo.model.Artist;
import com.tudor.demo.model.User;
import lombok.Data;

@Data
public class ArtistDTO {
    private User user;

    public static ArtistDTO fromEntity(Artist artist) {
        ArtistDTO dto = new ArtistDTO();
        dto.setUser(artist.getUser());
        return dto;
    }
}
