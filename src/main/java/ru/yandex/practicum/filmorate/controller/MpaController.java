package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.RatingMPA;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<RatingMPA> findAllGenres() {
        return mpaService.getAllGenres();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public RatingMPA findGenre(@PathVariable("id") Long id) {
        return mpaService.getGenreById(id);
    }
}
