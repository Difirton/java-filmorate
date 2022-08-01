package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Film newFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Film> findAllFilms() {
        return filmService.getAllFilms();
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public Film updateFilm(@PathVariable("id") Long id,
                                 @Valid @RequestBody Film film) {
        return filmService.updateFilm(id, film);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Film findOneFilm(@PathVariable("id") Long id) {
        return filmService.getFilmById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable("id") Long id) {
        filmService.removeFilmById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public Film updateFilmRootMapping(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film.getId(), film);
    }
}
