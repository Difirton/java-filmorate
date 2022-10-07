package ru.yandex.practicum.filmorate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.RatingMPA;
import ru.yandex.practicum.filmorate.service.RatingMpaService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class RatingMpaController {
    private final RatingMpaService ratingMpaService;

    @Operation(summary = "Get all movie ratings MPA", tags = "RatingMPA")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the movie ratings MPA",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<RatingMPA> findAllRatingsMpa() {
        return ratingMpaService.getAllRatingsMpa();
    }

    @Operation(summary = "Get the movie rating MPA by his id, which is specified in URL", tags = "RatingMPA")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the requested movie rating MPA",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public RatingMPA findRatingMpa(@PathVariable("id") @Parameter(description = "The rating MPA ID") Long id) {
        return ratingMpaService.getRatingsMpaById(id);
    }
}
