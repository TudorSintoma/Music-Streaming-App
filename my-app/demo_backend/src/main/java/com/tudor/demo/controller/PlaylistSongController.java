package com.tudor.demo.controller;

import com.tudor.demo.dto.PlaylistSongDTO;
import com.tudor.demo.dto.PlaylistSongResponseDTO;
import com.tudor.demo.model.Album;
import com.tudor.demo.model.PlaylistSong;
import com.tudor.demo.model.Song;
import com.tudor.demo.repository.PlaylistSongRepository;
import com.tudor.demo.service.PlaylistSongService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/playlist-songs")
public class PlaylistSongController {


    private PlaylistSongService playlistSongService;

    private PlaylistSongRepository playlistSongRepository;

    @GetMapping
    public List<PlaylistSong> getAll() {
        return playlistSongService.getAllPlaylistSongs();
    }

    @GetMapping("/playlist/{playlistId}")
    public ResponseEntity<List<PlaylistSongResponseDTO>> getSongsByPlaylist(@PathVariable Integer playlistId) {
        List<PlaylistSong> playlistSongs = playlistSongRepository.findByPlaylistId(playlistId);

        List<PlaylistSongResponseDTO> response = playlistSongs.stream()
                .map(ps -> {
                    Song song = ps.getSong();
                    if (song == null) return null; // safeguard

                    Album album = song.getAlbum(); // might be null
                    return new PlaylistSongResponseDTO(
                            song.getId(),
                            song.getName(),
                            album != null ? album.getId() : null,
                            album != null ? album.getName() : null,
                            album != null ? album.getImagePath() : null
                    );
                })
                .filter(Objects::nonNull) // remove nulls from list
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public PlaylistSong create(@RequestBody PlaylistSongDTO dto) {
        return playlistSongService.createPlaylistSong(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        playlistSongService.deletePlaylistSong(id);
        return ResponseEntity.noContent().build();
    }
}
