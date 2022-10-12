package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.entity.RatingMPA;
import ru.yandex.practicum.filmorate.error.exception.RatingMpaNotFoundException;
import ru.yandex.practicum.filmorate.repository.RatingMpaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingMpaService {
    private final RatingMpaRepository ratingMpaRepository;

    public List<RatingMPA> getAllRatingsMpa() {
        return ratingMpaRepository.findAll();
    }

    public RatingMPA getRatingsMpaById(Long id) {
        return ratingMpaRepository.findById(id).orElseThrow(() -> new RatingMpaNotFoundException(id));
    }
}
