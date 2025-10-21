package com.tudor.demo.service;

import com.tudor.demo.dto.PlaylistDTO;
import com.tudor.demo.model.Playlist;
import com.tudor.demo.model.User;
import com.tudor.demo.repository.PlaylistRepository;
import com.tudor.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Playlist> getAllPlaylists() {
        return playlistRepository.findAll();
    }

    public Optional<Playlist> getPlaylistById(Integer id) {
        return playlistRepository.findById(id);
    }

    public List<Playlist> getPlaylistsByUserId(int userId) {
        Optional<User> user = userRepository.findById(userId);
        return playlistRepository.findByUser(user);
    }


    public Playlist createPlaylist(PlaylistDTO dto) {
        return userRepository.findById(dto.getUserId())
                .map(user -> {
                    Playlist playlist = new Playlist();
                    playlist.setName(dto.getName());
                    playlist.setImagePath(dto.getImagePath());
                    playlist.setUser(user);
                    return playlistRepository.save(playlist);
                })
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public Playlist updatePlaylist(Integer id, PlaylistDTO dto) {
        return playlistRepository.findById(id)
                .flatMap(existing -> userRepository.findById(dto.getUserId())
                        .map(user -> {
                            existing.setName(dto.getName());
                            existing.setImagePath(dto.getImagePath());
                            existing.setUser(user);
                            return playlistRepository.save(existing);
                        }))
                .orElseThrow(() -> new IllegalArgumentException("Playlist or User not found"));
    }

    public void deletePlaylist(Integer id) {
        playlistRepository.deleteById(id);
    }
}
