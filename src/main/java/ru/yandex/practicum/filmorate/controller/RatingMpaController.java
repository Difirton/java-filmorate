package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.RatingMPA;
import ru.yandex.practicum.filmorate.service.RatingMpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class RatingMpaController {
    private final RatingMpaService ratingMpaService;

    @Autowired
    public RatingMpaController(RatingMpaService ratingMpaService) {
        this.ratingMpaService = ratingMpaService;
    }

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
