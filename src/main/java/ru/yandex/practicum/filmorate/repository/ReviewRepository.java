package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.Review;

import java.util.List;

public interface ReviewRepository extends StandardCRUDRepository<Review> {

    List<Review> findAll(Integer count);

    List<Review> findReviewsByFilmId(Long id, Integer count);

}
