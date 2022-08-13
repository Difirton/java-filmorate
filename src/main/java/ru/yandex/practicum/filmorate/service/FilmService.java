package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.error.FilmNotFoundException;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.util.List;

@Service
public class FilmService {
    private final FilmRepository filmRepository;
    private final UserService userService;

    @Autowired
    public FilmService(FilmRepository filmRepository, UserService userService) {
        this.filmRepository = filmRepository;
        this.userService = userService;
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
                    return filmRepository.save(f);
                })
                .orElseThrow(() -> new FilmNotFoundException(id));
    }

    public Film getFilmById(Long id) {
        return filmRepository.findById(id)
                .orElseThrow(() -> new FilmNotFoundException(id));
    }

    public void removeFilmById(Long id) {
        filmRepository.deleteById(id);
    }

    @Transactional
    public Film addLikeFilm(Long id, Long userId) {
        Film film = filmRepository.findById(id).orElseThrow(() -> new FilmNotFoundException(id));
        film.addUserLike(userService.getUserById(userId));
        filmRepository.save(film);
        return film;
    }

    @Transactional
    public void removeLikeFilm(Long id, Long userId) {
        Film film = filmRepository.findById(id).orElseThrow(() -> new FilmNotFoundException(id));
        film.removeUserLike(userService.getUserById(userId));
        filmRepository.save(film);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmRepository.findPopularFilms(count);
    }
}
