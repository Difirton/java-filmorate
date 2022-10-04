package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Review crateReview(@Valid @RequestBody Review review) {
        log.info("Request to create new review: " + review.toString());
        return reviewService.createReview(review);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info("Request to update review with id = {}, parameters to update: {}", review.getId(), review);
        return reviewService.updateReview(review.getId(), review);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable("id") Long id) {
        return reviewService.getReviewById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable("id") Long id) {
        log.info("Request to delete review with {}", id);
        reviewService.removeReviewById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Review> getReviewByFilmId(@RequestParam(name = "count") Optional<Integer> count,
                                          @RequestParam(name = "filmId") Optional<Long> filmId) {
        if (filmId.isPresent()) {
            return reviewService.getReviewsByFilmId(filmId.get(), count.orElse(10));
        } else {
            return reviewService.getAllReviews(count.orElse(10));
        }
    }
}
