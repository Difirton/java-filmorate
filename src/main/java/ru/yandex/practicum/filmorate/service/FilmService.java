package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.*;
import ru.yandex.practicum.filmorate.entity.binding.DirectorFilm;
import ru.yandex.practicum.filmorate.entity.binding.FilmGenre;
import ru.yandex.practicum.filmorate.error.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.error.FilmNotFoundException;
import ru.yandex.practicum.filmorate.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmRepository filmRepository;
    private final UserService userService;
    private final GenreRepository genreRepository;
    private final DirectorRepository directorRepository;
    private final FilmGenreRepository filmGenreRepository;
    private final DirectorFilmRepository directorFilmRepository;

    @Autowired
    public FilmService(FilmRepository filmRepository, UserService userService, GenreRepository genreRepository,
                       DirectorRepository directorRepository, FilmGenreRepository filmGenreRepository,
                       DirectorFilmRepository directorFilmRepository) {
        this.filmRepository = filmRepository;
        this.userService = userService;
        this.genreRepository = genreRepository;
        this.directorRepository = directorRepository;
        this.filmGenreRepository = filmGenreRepository;
        this.directorFilmRepository = directorFilmRepository;
    }

    @Transactional
    public Film createFilm(Film film) {
        film.setRate(0);
        return filmRepository.save(film);
    }

    public List<Film> getAllFilms() {
        List<Film> films = filmRepository.findAll();
        this.addGenresDirectorsInFilms(films);
        return films;
    }

    private void addGenresDirectorsInFilms(List<Film> films) {
        List<Genre> genres = genreRepository.findAll();
        List<FilmGenre> filmsGenres = filmGenreRepository.findAll();
        List<DirectorFilm> directorsFilms = directorFilmRepository.findAll();
        List<Director> directors = directorRepository.findAll();
        for (Film film : films) {
            List<Long> genresIds = filmsGenres.parallelStream()
                    .filter(fg -> fg.getFilmId().equals(film.getId()))
                    .map(FilmGenre::getGenreId)
                    .collect(Collectors.toList());
            film.setGenres(genres.parallelStream()
                    .filter(g -> genresIds.contains(g.getId()))
                    .collect(Collectors.toList()));
            List<Long> directorsIds = directorsFilms.parallelStream()
                    .filter(df -> df.getFilmId().equals(film.getId()))
                    .map(DirectorFilm::getDirectorId)
                    .collect(Collectors.toList());
            film.setDirectors(directors.parallelStream()
                    .filter(d -> directorsIds.contains(d.getId()))
                    .collect(Collectors.toList()));
        }
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
        List<Film> films = filmRepository.findPopularFilmsByRate(count);
        List<FilmGenre> filmsGenres = filmGenreRepository.findFilmsGenresByFilmsIds(films.stream()
                .map(Film::getId)
                .collect(Collectors.toList()));
        List<Genre> genres = genreRepository.findGenresByIds(filmsGenres.stream()
                .map(FilmGenre::getGenreId)
                .collect(Collectors.toList()));
        for (Film film : films) {
            List<Long> genresIds = filmsGenres.parallelStream()
                    .filter(fg -> fg.getFilmId().equals(film.getId()))
                    .map(FilmGenre::getGenreId)
                    .collect(Collectors.toList());
            film.setGenres(genres.parallelStream()
                    .filter(g -> genresIds.contains(g.getId()))
                    .collect(Collectors.toList()));
        }
        return films;
    }

    public List<Film> getDirectorsFilms(Long directorId, String param) {
        directorRepository.findById(directorId).orElseThrow(() -> new DirectorNotFoundException(directorId));
        List<Film> films;
        if (param.equals("noParam")) {
            films = filmRepository.findFilmsByDirectorId(directorId);
        } else if (param.equals("year") || param.equals("likes")) {
            films = filmRepository.findFilmsByDirectorId(directorId, param);
        } else {
            log.error("Invalid search query films by director's id with parameter: {}", param);
            throw new IllegalArgumentException("Invalid search query films by director's id with parameter: " + param);
        }
        this.addGenresDirectorsInFilms(films);
        return films;
    }

    public List<Film> searchFilms(String query, String by) {
        boolean byFilmName = by.contains("title");
        boolean byDirectorName = by.contains("director");

        if (!byFilmName && !byDirectorName)
            throw new IllegalArgumentException("Invalid parameter by: " + by);

        List<Film> films = new ArrayList<>();

        if (byFilmName)
            films.addAll(filmRepository.searchFilmsByName(query));
        if (byDirectorName)
            films.addAll(filmRepository.searchFilmsByDirectorName(query));

        this.addGenresDirectorsInFilms(films);
        films.sort((x, y) -> y.getRate() - x.getRate());
        return films;
    }
}
