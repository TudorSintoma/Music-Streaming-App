package com.tudor.demo.service;

import com.tudor.demo.dto.DiscoveryFilterDTO;
import com.tudor.demo.dto.UserDisplayDTO;
import com.tudor.demo.model.Artist;
import com.tudor.demo.model.Genre;
import com.tudor.demo.model.User;
import com.tudor.demo.repository.ArtistRepository;
import com.tudor.demo.specification.ArtistSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DiscoveryService {

    @Autowired
    private ArtistRepository artistRepository;

    public List<UserDisplayDTO> filterArtists(DiscoveryFilterDTO filter) {
        Specification<Artist> spec = Specification.where(ArtistSpecifications.nameContains(filter.getSearchQuery()))
                .and(ArtistSpecifications.hasGenreId(filter.getGenreId()))
                .and(ArtistSpecifications.hasPopularity(filter.getPopularity()))
                .and(ArtistSpecifications.hasDebutYearInCategory(filter.getYearCategory()));

        List<Artist> artists = artistRepository.findAll(spec, getSort(filter.getOrderBy()));

        return artists.stream()
                .map(Artist::getUser)
                .filter(Objects::nonNull)
                .map(user -> {
                    UserDisplayDTO dto = new UserDisplayDTO();
                    dto.setUserId(user.getUserId());
                    dto.setFullName(user.getFullName());
                    dto.setImagePath(user.getImagePath());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private Sort getSort(String orderBy) {
        System.out.println(orderBy);
        if (orderBy == null) return Sort.unsorted();

        switch (orderBy) {
            case "NAME":
                return Sort.by(Sort.Direction.ASC, "user.fullName");
            case "DEBUT_YEAR":
                return Sort.by(Sort.Direction.ASC, "year");
            case "POPULARITY":
                return Sort.by(Sort.Direction.ASC, "popularity");
            default:
                return Sort.unsorted();
        }
    }
}
