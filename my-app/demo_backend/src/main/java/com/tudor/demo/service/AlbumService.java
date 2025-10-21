package com.tudor.demo.service;

import com.tudor.demo.dto.AlbumDTO;
import com.tudor.demo.model.Album;
import com.tudor.demo.model.User;
import com.tudor.demo.repository.AlbumRepository;
import com.tudor.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;

    public AlbumService(AlbumRepository albumRepository, UserRepository userRepository) {
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
    }

    public List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }

    public Optional<Album> getAlbumById(Integer id) {
        return albumRepository.findById(id);
    }

    public Album createAlbum(AlbumDTO dto) {
        return userRepository.findById(dto.getUserId())
                .map(user -> {
                    Album album = new Album();
                    album.setName(dto.getName());
                    album.setReleaseYear(dto.getReleaseYear());
                    album.setUser(user);
                    return albumRepository.save(album);
                })
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public List<Album> getAlbumsByUserId(int userId) {
        return albumRepository.findByUserUserId(userId);
    }

    public Album updateAlbum(Integer id, AlbumDTO dto) {
        return albumRepository.findById(id)
                .map(existing -> userRepository.findById(dto.getUserId())
                        .map(user -> {
                            existing.setName(dto.getName());
                            existing.setReleaseYear(dto.getReleaseYear());
                            existing.setUser(user);
                            return albumRepository.save(existing);
                        })
                        .orElseThrow(() -> new IllegalArgumentException("User not found"))
                )
                .orElseThrow(() -> new IllegalArgumentException("Album not found"));
    }

    public void deleteAlbum(Integer id) {
        albumRepository.deleteById(id);
    }
}
