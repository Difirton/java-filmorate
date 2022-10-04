package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.error.FilmNotFoundException;
import ru.yandex.practicum.filmorate.error.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.error.UserNotFoundException;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository,
                         FilmRepository filmRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Review createReview(Review review) {
        filmRepository.findById(review.getFilmId()).orElseThrow(() -> new FilmNotFoundException(review.getFilmId()));
        userRepository.findById(review.getUserId()).orElseThrow(() -> new UserNotFoundException(review.getUserId()));
        return reviewRepository.save(review);
    }

    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
    }

    public List<Review> getAllReviews(Integer count) {
        return reviewRepository.findAll(count);
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
}
