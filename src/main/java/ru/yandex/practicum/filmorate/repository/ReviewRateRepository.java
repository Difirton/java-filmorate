package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.entity.ReviewRate;
import ru.yandex.practicum.filmorate.entity.User;

import java.util.List;

public interface ReviewRateRepository {

    ReviewRate save (User user, Review review, boolean isPositive);

    int delete(Long userId, Long reviewId, boolean isPositive);

    List<ReviewRate> getByReviewId(Long reviewId);

}
