package ru.yandex.practicum.filmorate.controller;

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

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<RatingMPA> findAllRatingsMpa() {
        return ratingMpaService.getAllRatingsMpa();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public RatingMPA findRatingMpa(@PathVariable("id") Long id) {
        return ratingMpaService.getRatingsMpaById(id);
    }
}
