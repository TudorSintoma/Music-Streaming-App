package com.tudor.demo.controller;

import com.tudor.demo.dto.SongDTO;
import com.tudor.demo.model.Song;
import com.tudor.demo.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/songs")
public class SongController {

    @Autowired
    private SongService songService;

    @GetMapping
    public List<Song> getAll() {
        return songService.getAllSongs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Song> getById(@PathVariable Integer id) {
        return songService.getSongById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/album/{albumId}")
    public ResponseEntity<List<Song>> getSongsByAlbumId(@PathVariable int albumId) {
        List<Song> songs = songService.getSongsByAlbumId(albumId);
        return ResponseEntity.ok(songs);
    }

    @PostMapping
    public Song create(@RequestBody SongDTO dto) {
        return songService.createSong(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Song> update(@PathVariable Integer id, @RequestBody SongDTO dto) {
        try {
            return ResponseEntity.ok(songService.updateSong(id, dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        songService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }
}
