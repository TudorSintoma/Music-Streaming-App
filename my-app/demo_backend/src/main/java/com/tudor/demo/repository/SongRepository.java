package com.tudor.demo.repository;

import com.tudor.demo.model.Album;
import com.tudor.demo.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Integer> {
    List<Song> findByAlbumId(int id);
}
