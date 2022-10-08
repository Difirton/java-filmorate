package ru.yandex.practicum.filmorate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name="The user review API", description="API for interacting with endpoints associated with user review")
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(summary = "Creates a new review", tags = "review")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "The review was created",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Review crateReview(@Valid @RequestBody Review review) {
        log.info("Request to create new review: " + review.toString());
        return reviewService.createReview(review);
    }

    @Operation(summary = "Update the review by it's id, which is specified in the json body", tags = "review")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The review was updated",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info("Request to update review with id = {}, parameters to update: {}", review.getId(), review);
        return reviewService.updateReview(review.getId(), review);
    }

    @Operation(summary = "Get the review by it's id, which is specified in URL", tags = "review")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the requested review",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable("id") @Parameter(description = "The review ID") Long id) {
        return reviewService.getReviewById(id);
    }

    @Operation(summary = "Removes the review by it's id, which is specified in URL", tags = "review")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The review was removed",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable("id") @Parameter(description = "The review ID") Long id) {
        log.info("Request to delete review with {}", id);
        reviewService.removeReviewById(id);
    }

    @Operation(summary = "Get the required number of reviews by the movie id specified in the URL", tags = "review")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the required number requested review by the movie id",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Review> getReviewByFilmId(
            @RequestParam(name = "count") @Parameter(description = "The number of reviews") Optional<Integer> count,
            @RequestParam(name = "filmId") @Parameter(description = "The film ID") Optional<Long> filmId) {
        return reviewService.getReviews(filmId, count);
    }

    @Operation(summary = "The user likes the review", tags = "review")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the likes review",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}/like/{userId}")
    public Review addLike(@PathVariable(name = "id") @Parameter(description = "The review ID") Long reviewId,
                          @PathVariable(name = "userId") @Parameter(description = "The user ID") Long userId) {
        return reviewService.addLike(reviewId, userId);
    }

    @Operation(summary = "The user the dislike on the review", tags = "review")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the dislikes review",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}/dislike/{userId}")
    public Review addDislike(@PathVariable(name = "id") @Parameter(description = "The review ID") Long reviewId,
                             @PathVariable(name = "userId") @Parameter(description = "The user ID") Long userId) {
        return reviewService.addDislike(reviewId, userId);
    }

    @Operation(summary = "The user removes the like on the review", tags = "review")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the review without like",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}/like/{userId}")
    public Review deleteLike(@PathVariable(name = "id") @Parameter(description = "The review ID") Long reviewId,
                             @PathVariable(name = "userId") @Parameter(description = "The user ID") Long userId) {
        return reviewService.removeLike(reviewId, userId);
    }

    @Operation(summary = "The user removes the dislike on the review", tags = "review")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the review without dislike",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}/dislike/{userId}")
    public Review deleteDislike(@PathVariable(name = "id") @Parameter(description = "The review ID") Long reviewId,
                                @PathVariable(name = "userId") @Parameter(description = "The user ID") Long userId) {
        return reviewService.removeDislike(reviewId, userId);
    }
}
