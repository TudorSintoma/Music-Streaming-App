package com.tudor.demo.controller;

import com.tudor.demo.dto.PlaylistDTO;
import com.tudor.demo.model.Playlist;
import com.tudor.demo.model.Song;
import com.tudor.demo.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/playlists")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    @GetMapping
    public List<Playlist> getAll() {
        return playlistService.getAllPlaylists();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Playlist> getById(@PathVariable Integer id) {
        return playlistService.getPlaylistById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Playlist>> getPlaylistsByUserId(@PathVariable int userId) {
        List<Playlist> playlists = playlistService.getPlaylistsByUserId(userId);
        return ResponseEntity.ok(playlists);
    }

    @PostMapping
    public Playlist create(@RequestBody PlaylistDTO dto) {
        return playlistService.createPlaylist(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Playlist> update(@PathVariable Integer id, @RequestBody PlaylistDTO dto) {
        try {
            return ResponseEntity.ok(playlistService.updatePlaylist(id, dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.noContent().build();
    }
}
