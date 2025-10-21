package com.tudor.demo.service;

import com.tudor.demo.model.Genre;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GenreService {

    public List<Map<String, Object>> findAllGenreIdAndImagePath() {
        List<Map<String, Object>> genreList = new ArrayList<>();

        for (Genre genre : Genre.values()) {
            Map<String, Object> genreMap = new HashMap<>();
            genreMap.put("id", genre.getId());
            if(genre.name().equalsIgnoreCase("hiphop")) {
                genreMap.put("imagePath", "/images/genres/hip-hop/hiphop.jpg");
            }else {
                genreMap.put("imagePath", "/images/genres/" + genre.name().toLowerCase() + "/" + genre.name().toLowerCase() + ".jpg");
            }
            genreList.add(genreMap);
        }
        return genreList;
    }
}
