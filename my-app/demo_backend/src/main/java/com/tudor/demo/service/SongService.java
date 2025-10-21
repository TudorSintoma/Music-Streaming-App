package com.tudor.demo.service;

import com.tudor.demo.dto.SongDTO;
import com.tudor.demo.model.Album;
import com.tudor.demo.model.Song;
import com.tudor.demo.repository.AlbumRepository;
import com.tudor.demo.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private AlbumRepository albumRepository;

    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    public Optional<Song> getSongById(Integer id) {
        return songRepository.findById(id);
    }

    public List<Song> getSongsByAlbumId(int albumId) {
        return songRepository.findByAlbumId(albumId);
    }

    public Song createSong(SongDTO dto) {
        return albumRepository.findById(dto.getAlbumId())
                .map(album -> {
                    Song song = new Song();
                    song.setName(dto.getName());
                    song.setLikes(dto.getLikes());
                    song.setAlbum(album);
                    return songRepository.save(song);
                })
                .orElseThrow(() -> new IllegalArgumentException("Album not found"));
    }

    public Song updateSong(Integer id, SongDTO dto) {
        return songRepository.findById(id)
                .flatMap(existing -> albumRepository.findById(dto.getAlbumId())
                        .map(album -> {
                            existing.setName(dto.getName());
                            existing.setLikes(dto.getLikes());
                            existing.setAlbum(album);
                            return songRepository.save(existing);
                        })
                )
                .orElseThrow(() -> new IllegalArgumentException("Song or Album not found"));
    }

    public void deleteSong(Integer id) {
        songRepository.deleteById(id);
    }
}
