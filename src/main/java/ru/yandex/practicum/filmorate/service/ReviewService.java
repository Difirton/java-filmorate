package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.entity.ReviewRate;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.error.FilmNotFoundException;
import ru.yandex.practicum.filmorate.error.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.error.UserNotFoundException;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.ReviewRateRepository;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final ReviewRateRepository reviewRateRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository,
                         FilmRepository filmRepository,
                         UserRepository userRepository,
                         ReviewRateRepository reviewRateRepository) {
        this.reviewRepository = reviewRepository;
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
        this.reviewRateRepository = reviewRateRepository;
    }

    @Transactional
    public Review createReview(Review review) {
        filmRepository.findById(review.getFilmId()).orElseThrow(() -> new FilmNotFoundException(review.getFilmId()));
        userRepository.findById(review.getUserId()).orElseThrow(() -> new UserNotFoundException(review.getUserId()));
        return reviewRepository.save(review);
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

    public List<Review> getAllReviews(Integer count) {
        List<Review> reviews = reviewRepository.findAll(count);
        List<ReviewRate> rates = reviewRateRepository.getByReviewIds(reviews.stream()
                .map(Review::getId)
                .collect(Collectors.toList()));
        return reviews.stream()
                .peek(review -> review.setUsersRates(rates.stream()
                        .filter(rate -> rate.getReview().getId().equals(review.getId()))
                        .map(ReviewRate::getUser)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    public List<Review> getReviewsByFilmId(Long filmId, Integer count) {
        return reviewRepository.findReviewsByFilmId(filmId, count);
    }

    @Transactional
    public Review updateReview(Long id, Review newReview) {
        filmRepository.findById(newReview.getFilmId()).orElseThrow(() -> new FilmNotFoundException(newReview.getFilmId()));
        userRepository.findById(newReview.getUserId()).orElseThrow(() -> new UserNotFoundException(newReview.getUserId()));
        return reviewRepository.findById(id)
                .map(r -> {
                    r.setContent(newReview.getContent());
                    r.setIsPositive(newReview.getIsPositive());
                    r.setUserId(newReview.getUserId());
                    r.setFilmId(newReview.getFilmId());
                    r.setUseful(newReview.getUseful());
                    return reviewRepository.update(r);
                })
                .orElseThrow(() -> new ReviewNotFoundException(id));
    }

    public void removeReviewById(Long id) {
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
