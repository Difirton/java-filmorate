package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.RatingMPA;

import java.util.List;
import java.util.Optional;

public interface RatingMpaRepository {

    RatingMPA save(RatingMPA ratingMPA);

    int update(RatingMPA ratingMPA);

    int deleteById(Long id);

    List<RatingMPA> findAll();

    Optional<RatingMPA> findById(Long id);

    int[] saveAll(List<RatingMPA> ratingsMPA);
}
