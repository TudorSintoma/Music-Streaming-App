package com.tudor.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "artists")
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_id")
    private Integer artistId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "genre_id", nullable = false)
    private Integer genreId;

    @Enumerated(EnumType.STRING)
    @Column(name = "popularity", nullable = false)
    private Popularity popularity;

    @Column(name = "debut_year", nullable = false)
    private Integer year;

    public Genre getGenre() {
        return Genre.fromId(this.genreId);
    }

    public void setGenre(Genre genre) {
        this.genreId = genre.getId();
    }
}
