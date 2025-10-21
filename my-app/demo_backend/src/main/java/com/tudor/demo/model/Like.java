package com.tudor.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@IdClass(LikeId.class)
@Data
@Table(name = "likes")
public class Like {
    @Id
    private int userId;

    @Id
    private int songId;

    @ManyToOne
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "songId", insertable = false, updatable = false)
    private Song song;
}
