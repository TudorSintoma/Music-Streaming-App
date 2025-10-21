package com.tudor.demo.service;

import com.tudor.demo.dto.PlaylistSongDTO;
import com.tudor.demo.model.Playlist;
import com.tudor.demo.model.PlaylistSong;
import com.tudor.demo.model.Song;
import com.tudor.demo.repository.PlaylistRepository;
import com.tudor.demo.repository.PlaylistSongRepository;
import com.tudor.demo.repository.SongRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaylistSongService {

    @Autowired
    private PlaylistSongRepository playlistSongRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private SongRepository songRepository;

    public List<PlaylistSong> getAllPlaylistSongs() {
        return playlistSongRepository.findAll();
    }

    @Transactional
    public PlaylistSong createPlaylistSong(PlaylistSongDTO dto) {
        Playlist playlist = playlistRepository.findById(dto.getPlaylistId())
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found"));

        Song song = songRepository.findById(dto.getSongId())
                .orElseThrow(() -> new IllegalArgumentException("Song not found"));

        PlaylistSong playlistSong = new PlaylistSong();
        playlistSong.setPlaylist(playlist);
        playlistSong.setSong(song);
        playlist.getPlaylistSongs().add(playlistSong);

        return playlistSongRepository.save(playlistSong);
    }


    public void deletePlaylistSong(Integer id) {
        playlistSongRepository.deleteById(id);
    }
}