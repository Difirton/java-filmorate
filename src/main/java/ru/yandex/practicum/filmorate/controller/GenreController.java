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
import ru.yandex.practicum.filmorate.entity.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
@Tag(name="The movie genres API", description="API for interacting with endpoints associated with movie genres")
public class GenreController {
    private final GenreService genreService;

    @Operation(summary = "Get all movie genres", tags = "genre")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the genres",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Genre> findAllGenres() {
        return genreService.getAllGenres();
    }

    @Operation(summary = "Get the genre by his id, which is specified in URL", tags = "genre")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the requested genre",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Genre findGenre(@PathVariable("id") @Parameter(description = "The genre ID") Long id) {
        return genreService.getGenreById(id);
    }
}
