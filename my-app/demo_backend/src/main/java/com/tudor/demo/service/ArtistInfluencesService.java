package com.tudor.demo.service;

import com.tudor.demo.dto.ArtistInfluencesDTO;
import com.tudor.demo.exception.ResourceNotFoundException;
import com.tudor.demo.model.Artist;
import com.tudor.demo.model.ArtistInfluence;
import com.tudor.demo.repository.ArtistInfluencesRepository;
import com.tudor.demo.repository.ArtistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtistInfluencesService {

    private final ArtistInfluencesRepository artistInfluencesRepository;
    private final ArtistRepository artistRepository;

    public ArtistInfluencesService(ArtistInfluencesRepository artistInfluencesRepository, ArtistRepository artistRepository) {
        this.artistInfluencesRepository = artistInfluencesRepository;
        this.artistRepository = artistRepository;
    }

    public ArtistInfluence addInfluence(ArtistInfluencesDTO dto) {
        Artist artist = artistRepository.findById(dto.getArtistId())
                .orElseThrow(() -> new ResourceNotFoundException("Artist with ID " + dto.getArtistId() + " not found"));

        Artist influencedBy = artistRepository.findById(dto.getInfluencedById())
                .orElseThrow(() -> new ResourceNotFoundException("Influenced artist with ID " + dto.getInfluencedById() + " not found"));

        ArtistInfluence influence = new ArtistInfluence();
        influence.setArtist(artist);
        influence.setInfluencedBy(influencedBy);

        return artistInfluencesRepository.save(influence);
    }

    public List<ArtistInfluence> getAllInfluences() {
        return artistInfluencesRepository.findAll();
    }

    public ArtistInfluence updateInfluence(Integer id, ArtistInfluencesDTO dto) {
        ArtistInfluence existingInfluence = artistInfluencesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Influence with ID " + id + " not found"));

        Artist artist = artistRepository.findById(dto.getArtistId())
                .orElseThrow(() -> new ResourceNotFoundException("Artist with ID " + dto.getArtistId() + " not found"));

        Artist influencedBy = artistRepository.findById(dto.getInfluencedById())
                .orElseThrow(() -> new ResourceNotFoundException("Influenced artist with ID " + dto.getInfluencedById() + " not found"));

        existingInfluence.setArtist(artist);
        existingInfluence.setInfluencedBy(influencedBy);

        return artistInfluencesRepository.save(existingInfluence);
    }

    public void deleteInfluence(Integer id) {
        ArtistInfluence influence = artistInfluencesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Influence with ID " + id + " not found"));

        artistInfluencesRepository.delete(influence);
    }

    public List<ArtistInfluence> getInfluencesByArtist(Artist artist) {
        return artistInfluencesRepository.findByArtist(artist);
    }

    public List<ArtistInfluence> getInfluencesByInfluencedBy(Artist influencedBy) {
        return artistInfluencesRepository.findByInfluencedBy(influencedBy);
    }
}
