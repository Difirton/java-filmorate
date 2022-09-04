package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Genre> findAllGenres() {
        return genreService.getAllGenres();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Genre findGenre(@PathVariable("id") Long id) {
        return genreService.getGenreById(id);
    }
}
