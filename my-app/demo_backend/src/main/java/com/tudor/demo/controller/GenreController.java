package com.tudor.demo.controller;

import com.tudor.demo.dto.GenreInfoDTO;
import com.tudor.demo.model.Genre;
import com.tudor.demo.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/genres")
    public List<Map<String, Object>> getGenres() {
        return genreService.findAllGenreIdAndImagePath();
    }

    @GetMapping("/genres/names")
    public Map<Integer, String> getGenreNames() {
        return Arrays.stream(Genre.values())
                .collect(Collectors.toMap(Genre::getId, Genre::name));
    }
}
