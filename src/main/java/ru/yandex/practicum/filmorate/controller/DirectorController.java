package ru.yandex.practicum.filmorate.controller;

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
public class DirectorController {

    private final DirectorService directorService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Director newDirector(@Valid @RequestBody Director director) {
        log.info("Request to create new director: " + director.toString());
        return directorService.createDirector(director);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Director findDirector(@PathVariable("id") Long id) {
        return directorService.getDirectorById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Director> findAll() {
        return directorService.getAllDirectors();
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public Director updateDirector(@PathVariable("id") Long id, @Valid @RequestBody Director director) {
        log.info("Request to update director with id = {}, parameters to update: {}", id, director.toString());
        return directorService.updateDirector(id, director);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        log.info("Request to update director with id = {}, parameters to update: {}", director.getId(), director);
        return directorService.updateDirector(director.getId(), director);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable("id") Long id) {
        log.info("Request to delete director with {}", id);
        directorService.removeDirectorById(id);
    }
}
