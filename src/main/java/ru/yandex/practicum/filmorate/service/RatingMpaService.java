package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.entity.RatingMPA;
import ru.yandex.practicum.filmorate.error.exception.RatingMpaNotFoundException;
import ru.yandex.practicum.filmorate.repository.RatingMpaRepository;

import java.util.List;

@Service
public class RatingMpaService {
    private final RatingMpaRepository ratingMpaRepository;

    @Autowired
    public RatingMpaService(RatingMpaRepository ratingMpaRepository) {
        this.ratingMpaRepository = ratingMpaRepository;
    }

    public List<RatingMPA> getAllRatingsMpa() {
        return ratingMpaRepository.findAll();
    }

    public RatingMPA getRatingsMpaById(Long id) {
        return ratingMpaRepository.findById(id).orElseThrow(() -> new RatingMpaNotFoundException(id));
    }
}
