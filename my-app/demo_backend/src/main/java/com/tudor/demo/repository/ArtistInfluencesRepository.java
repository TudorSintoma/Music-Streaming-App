package com.tudor.demo.repository;

import com.tudor.demo.model.Artist;
import com.tudor.demo.model.ArtistInfluence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistInfluencesRepository extends JpaRepository<ArtistInfluence, Integer> {

    List<ArtistInfluence> findByArtist(Artist artist);

    List<ArtistInfluence> findByInfluencedBy(Artist influencedBy);
}
