package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.error.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.error.FilmNotFoundException;
import ru.yandex.practicum.filmorate.repository.DirectorRepository;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmRepository filmRepository;
    private final UserService userService;
    private final GenreRepository genreRepository;
    private final DirectorRepository directorRepository;

    @Autowired
    public FilmService(FilmRepository filmRepository, UserService userService, GenreRepository genreRepository,
                       DirectorRepository directorRepository) {
        this.filmRepository = filmRepository;
        this.userService = userService;
        this.genreRepository = genreRepository;
        this.directorRepository = directorRepository;
    }

    @Transactional
    public Film createFilm(Film film) {
        return filmRepository.save(film);
    }

    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    @Transactional
    public Film updateFilm(Long id, Film newFilm) {
        return filmRepository.findById(id)
                .map(f -> {
                    f.setName(newFilm.getName());
                    f.setDescription(newFilm.getDescription());
                    f.setReleaseDate(newFilm.getReleaseDate());
                    f.setDuration(newFilm.getDuration());
                    f.setRatingMPA(newFilm.getRatingMPA());
                    f.setGenres(newFilm.getGenres().stream()
                            .distinct()
                            .collect(Collectors.toList()));
                    f.setUsersLikes(newFilm.getUsersLikes());
                    f.setDirectors(newFilm.getDirectors());
                    return filmRepository.update(f);
                })
                .orElseThrow(() -> new FilmNotFoundException(id));
    }

    public Film getFilmById(Long id) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new FilmNotFoundException(id));
        film.setGenres(genreRepository.findGenresByFilmId(id));
        return film;
    }

    public void removeFilmById(Long id) {
        filmRepository.deleteById(id);
    }

    @Transactional
    public Film addLikeFilm(Long id, Long userId) {
        Film film = filmRepository.findById(id).orElseThrow(() -> new FilmNotFoundException(id));
        if (film.getUsersLikes().stream()
                .map(User::getId)
                .anyMatch(i -> i.equals(userId))) {
            return film;
        }
        film.addUserLike(userService.getUserById(userId));
        return filmRepository.update(film);
    }

    @Transactional
    public void removeLikeFilm(Long id, Long userId) {
        Film film = filmRepository.findById(id).orElseThrow(() -> new FilmNotFoundException(id));
        film.removeUserLike(userService.getUserById(userId));
        filmRepository.update(film);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmRepository.findPopularFilmsByRate(count);
    }

    public List<Film> getDirectorsFilms(Long directorId, String param) {
        directorRepository.findById(directorId).orElseThrow(() -> new DirectorNotFoundException(directorId));
        if (param.equals("noParam")) {
            return filmRepository.findFilmsByDirectorId(directorId);
        } else if (param.equals("year") || param.equals("likes")) {
            return filmRepository.findFilmsByDirectorId(directorId, param);
        } else {
            log.error("Invalid search query films by director's id with parameter: {}", param);
            throw new IllegalArgumentException("Invalid search query films by director's id with parameter: " + param);
        }
    }
}
