package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Film newFilm(@Valid @RequestBody Film film) {
        log.info("Request to create new film: " + film.toString());
        return filmService.createFilm(film);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Film> findAllFilms() {
        return filmService.getAllFilms();
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public Film updateFilm(@PathVariable("id") Long id, @Valid @RequestBody Film film) {
        log.info("Request to update film with id = {}, parameters to update: {}", id ,film.toString());
        return filmService.updateFilm(id, film);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Film findFilm(@PathVariable("id") Long id) {
        return filmService.getFilmById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable("id") Long id) {
        log.info("Request to delete film with {}", id);
        filmService.removeFilmById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public Film updateFilmRootMapping(@Valid @RequestBody Film film) {
        log.info("Request to update film with id = {}, parameters to update: {}", film.getId() ,film);
        return filmService.updateFilm(film.getId(), film);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("{id}/like/{userId}")
    public Film addLikeFilm(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("Request to add like to film with id = {} from user with id = {}", id, userId);
        return filmService.addLikeFilm(id, userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("{id}/like/{userId}")
    public void removeLikeFilm(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("Request to remove like user with id = {} of film with id = {}", userId, id);
        filmService.removeLikeFilm(id, userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("popular")
    public List<Film> getPopularFilms(@RequestParam(name = "count") Optional<Integer> count) {
        return filmService.getPopularFilms(count.orElse(10));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilmsWithSort(@PathVariable("directorId") Long id,
                                               @RequestParam(name = "sortBy") Optional<String> param) {
        return filmService.getDirectorsFilms(id, param.orElse("noParam"));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("common")
    public List<Film> getCommonFilms(@RequestParam(name = "userId") Long userId,
                                     @RequestParam(name = "friendId") Long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }
}
