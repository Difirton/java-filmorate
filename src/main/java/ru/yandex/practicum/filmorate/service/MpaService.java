package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.entity.RatingMPA;
import ru.yandex.practicum.filmorate.error.RatingMpaNotFoundException;
import ru.yandex.practicum.filmorate.repository.RatingMpaRepository;

import java.util.List;

@Service
public class MpaService {
    private final RatingMpaRepository ratingMpaRepository;

    @Autowired
    public MpaService(RatingMpaRepository ratingMpaRepository) {
        this.ratingMpaRepository = ratingMpaRepository;
    }

    public List<RatingMPA> getAllGenres() {
        return ratingMpaRepository.findAll();
    }

    public RatingMPA getGenreById(Long id) {
        return ratingMpaRepository.findById(id).orElseThrow(() -> new RatingMpaNotFoundException(id));
    }
}
