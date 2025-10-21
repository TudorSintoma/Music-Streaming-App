package com.tudor.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "listeners")
public class Listener {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "listener_id")
    private Integer listenerId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "likes_playlist")
    private Playlist likesPlaylist;

    @OneToOne
    @JoinColumn(name = "discover_playlist")
    private Playlist discoverPlaylist;
}
