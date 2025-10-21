package com.tudor.demo.repository;

import com.tudor.demo.model.PlaylistSong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, Integer> {
    List<PlaylistSong> findByPlaylistId(Integer playlistId);
}
