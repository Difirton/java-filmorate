package ru.yandex.practicum.filmorate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
@Tag(name="The film API", description="API for interacting with endpoints associated with films")
public class FilmController {
    private final FilmService filmService;

    @Operation(summary = "Creates a new film", tags = "film")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "The film was created",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @Operation(summary = "Gets all films", tags = "film")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the films",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Film> findAllFilms() {
        return filmService.getAllFilms();
    }

    @Operation(summary = "Update the film by it's id, which is specified in URL", tags = "film")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The film was updated",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public Film updateFilm(@PathVariable("id") @Parameter(description = "The film ID") Long id,
                               @Valid @RequestBody Film film) {
        return filmService.updateFilm(id, film);
    }

    @Operation(summary = "Get the film by it's id, which is specified in URL", tags = "film")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the requested film",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable("id") @Parameter(description = "The film ID") Long id) {
        return filmService.getFilmById(id);
    }

    @Operation(summary = "Removes the film by it's id, which is specified in URL", tags = "film")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The film was removed",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable("id") @Parameter(description = "The film ID") Long id) {
        filmService.removeFilmById(id);
    }

    @Operation(summary = "Update the film by it's id, which is specified in his json body", tags = {"film", "like"})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The film was updated",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film.getId(), film);
    }

    @Operation(summary = "The user make mark the film", tags = {"film", "like"})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the likes film",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("{id}/like/{userId}")
    public Film addMarkFilm(@PathVariable("id") @Parameter(description = "The film ID") Long id,
                            @PathVariable("userId") @Parameter(description = "The user ID") Long userId,
                            @PathVariable("mark") @Parameter(description = "The mark of film") Integer mark) {
        return filmService.addFilmMark(id, userId, mark);
    }

    @Operation(summary = "The user removes the mark on the film", tags = "film")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the film without like",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("{id}/like/{userId}")
    public void removeMarkFilm(@PathVariable("id") @Parameter(description = "The film ID") Long id,
                               @PathVariable("userId") @Parameter(description = "The user ID") Long userId) {
        filmService.removeMarkFilm(id, userId);
    }

    @Operation(summary = "Get a list of the most popular films of the specified genre for the given year",
            tags = {"film", "genre"})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found a list of the most popular films",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("popular")
    public List<Film> getPopularFilms(
            @RequestParam(name = "count") @Parameter(description = "The number of films") Optional<Integer> count,
            @RequestParam(name = "genreId") @Parameter(description = "The film genre ID") Optional<Integer> genreId,
            @RequestParam(name = "year") @Parameter(description = "Film release year") Optional<Integer> year) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    @Operation(summary = "Get a list of director's films sorted by number of likes or year of release",
            tags = {"film", "director"})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found a sorted list of director's films",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilmsWithSort(
            @PathVariable("directorId")  Long id,
            @RequestParam(name = "sortBy") @Parameter(description = "Choice of sorting option, possible parameters: " +
                    "year, likes. The field may remain empty.") Optional<String> param) {
        return filmService.getDirectorsFilms(id, param.orElse("noParam"));
    }

    @Operation(summary = "Get a list of shared user movies sorted by popularity", tags = {"film", "user"})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found a sorted list of shared user movies",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("common")
    public List<Film> getCommonFilms(
            @RequestParam(name = "userId") @Parameter(description = "The user ID") Long userId,
            @RequestParam(name = "friendId") @Parameter(description = "The another user ID") Long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @Operation(summary = "Get a list of movies sorted by popularity and search criteria", tags = {"film", "director"},
            description = "Returns a list of movies sorted by popularity. Can take the values director " +
                    "(search by director), title (search by title), or both values separated by commas when searching " +
                    "both by director and title")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found a list of movies sorted by popularity",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam String query, @RequestParam String by) {
        List<String> byFields = Arrays.asList(by.toLowerCase().split(","));
        return filmService.searchFilms(query, byFields);
    }
}
