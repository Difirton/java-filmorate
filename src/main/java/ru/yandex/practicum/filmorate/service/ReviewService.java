package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.*;
import ru.yandex.practicum.filmorate.entity.constant.EventType;
import ru.yandex.practicum.filmorate.entity.constant.Operation;
import ru.yandex.practicum.filmorate.error.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.error.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.error.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.repository.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final ReviewRateRepository reviewRateRepository;
    private final EventService eventService;

    @Transactional
    public Review createReview(Review review) {
        filmRepository.findById(review.getFilmId()).orElseThrow(() -> new FilmNotFoundException(review.getFilmId()));
        userRepository.findById(review.getUserId()).orElseThrow(() -> new UserNotFoundException(review.getUserId()));
        Review newReview = reviewRepository.save(review);
        eventService.createEvent(newReview.getUserId(), EventType.REVIEW, Operation.ADD, newReview.getId());
        return newReview;
    }

    public Review getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
        review.setUsersRates(reviewRateRepository.getByReviewIds(List.of(id))
                .stream()
                .map(ReviewRate::getUser)
                .collect(Collectors.toList()));
        return review;
    }

    private List<Review> getAllReviews(Integer count) {
        List<Review> reviews = reviewRepository.findAll(count);
        List<ReviewRate> rates = reviewRateRepository.getByReviewIds(reviews.stream()
                .map(Review::getId)
                .collect(Collectors.toList()));

        for (Review review : reviews) {
            review.setUsersRates(rates.stream()
                    .filter(rate -> rate.getReview().getId().equals(review.getId()))
                    .map(ReviewRate::getUser)
                    .collect(Collectors.toList()));
        }
        return reviews;
    }

    private List<Review> getReviewsByFilmId(Long filmId, Integer count) {
        return reviewRepository.findReviewsByFilmId(filmId, count);
    }

    public List<Review> getReviews(Optional<Long> filmId, Optional<Integer> count) {
        if (filmId.isPresent()) {
            return this.getReviewsByFilmId(filmId.get(), count.orElse(10));
        } else {
            return this.getAllReviews(count.orElse(10));
        }
    }

    @Transactional
    public Review updateReview(Long id, Review newReview) {
        filmRepository.findById(newReview.getFilmId()).orElseThrow(() -> new FilmNotFoundException(newReview.getFilmId()));
        userRepository.findById(newReview.getUserId()).orElseThrow(() -> new UserNotFoundException(newReview.getUserId()));
        Review updatedReview = reviewRepository.findById(id)
                .map(r -> {
                    r.setContent(newReview.getContent());
                    r.setIsPositive(newReview.getIsPositive());
                    r.setUserId(newReview.getUserId());
                    r.setFilmId(newReview.getFilmId());
                    r.setUseful(newReview.getUseful());
                    return reviewRepository.update(r);
                })
                .orElseThrow(() -> new ReviewNotFoundException(id));
        eventService.createEvent(id, EventType.REVIEW, Operation.UPDATE, newReview.getId());
        return updatedReview;
    }

    public void removeReviewById(Long id) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new ReviewNotFoundException(id));
        eventService.createEvent(review.getUserId(), EventType.REVIEW, Operation.REMOVE, review.getId());
        reviewRepository.deleteById(id);
    }

    @Transactional
    public Review addLike(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        reviewRateRepository.save(user, review, true);
        review.addLike(user);
        return review;
    }

    @Transactional
    public Review addDislike(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        reviewRateRepository.save(user, review, false);
        review.addDislike(user);
        return review;
    }

    @Transactional
    public Review removeLike(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        reviewRateRepository.delete(user.getId(), review.getId(), true);
        review.removeLike(user);
        return review;
    }

    @Transactional
    public Review removeDislike(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        reviewRateRepository.delete(user.getId(), review.getId(), false);
        review.removeDislike(user);
        return review;
    }
}
