package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.error.FilmNotFoundException;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmRepository filmRepository;
    private final UserService userService;
    private final GenreRepository genreRepository;

    @Autowired
    public FilmService(FilmRepository filmRepository, UserService userService, GenreRepository genreRepository) {
        this.filmRepository = filmRepository;
        this.userService = userService;
        this.genreRepository = genreRepository;
    }

    public Film createFilm(Film film) {
        return filmRepository.save(film);
    }

    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }

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
}
