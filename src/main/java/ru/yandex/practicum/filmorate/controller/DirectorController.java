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
import ru.yandex.practicum.filmorate.entity.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
@Tag(name="The Director API", description="API for interacting with endpoints associated with directors")
public class DirectorController {

    private final DirectorService directorService;

    @Operation(summary = "Creates a new director", tags = "director")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "The director was created",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Director newDirector(@Valid @RequestBody Director director) {
        log.info("Request to create new director: " + director.toString());
        return directorService.createDirector(director);
    }

    @Operation(summary = "Get the director by his id, which is specified in URL", tags = "director")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the requested director",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Director findDirector(@PathVariable("id") @Parameter(description = "The director ID") Long id) {
        return directorService.getDirectorById(id);
    }

    @Operation(summary = "Gets all directors", tags = "director")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the directors",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Director> findAll() {
        return directorService.getAllDirectors();
    }

    @Operation(summary = "Update the director by his id, which is specified in URL", tags = "director")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The director was updated",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public Director updateDirector(@PathVariable("id") @Parameter(description = "The director ID") Long id,
                                   @Valid @RequestBody Director director) {
        log.info("Request to update director with id = {}, parameters to update: {}", id, director.toString());
        return directorService.updateDirector(id, director);
    }

    @Operation(summary = "Update the director by his id, which is specified in his json body", tags = "director")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The director was updated",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        log.info("Request to update director with id = {}, parameters to update: {}", director.getId(), director);
        return directorService.updateDirector(director.getId(), director);
    }

    @Operation(summary = "Removes the director by his id, which is specified in URL", tags = "director")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The director was removed",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable("id") @Parameter(description = "The director ID") Long id) {
        log.info("Request to delete director with {}", id);
        directorService.removeDirectorById(id);
    }
}
