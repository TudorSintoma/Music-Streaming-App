package com.tudor.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "artist_influences")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtistInfluence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @ManyToOne
    @JoinColumn(name = "influenced_by", nullable = false)
    private Artist influencedBy;
}
