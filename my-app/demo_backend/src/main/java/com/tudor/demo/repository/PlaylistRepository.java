package com.tudor.demo.repository;

import com.tudor.demo.model.Playlist;
import com.tudor.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {
    List<Playlist> findByUser(Optional<User> user);
}
