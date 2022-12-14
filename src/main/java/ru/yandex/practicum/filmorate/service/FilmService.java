package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.*;
import ru.yandex.practicum.filmorate.entity.binding.DirectorFilm;
import ru.yandex.practicum.filmorate.entity.binding.FilmGenre;
import ru.yandex.practicum.filmorate.entity.constant.EventType;
import ru.yandex.practicum.filmorate.entity.constant.Operation;
import ru.yandex.practicum.filmorate.error.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.error.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.error.exception.MarkNotFoundException;
import ru.yandex.practicum.filmorate.repository.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;
    private final UserService userService;
    private final GenreRepository genreRepository;
    private final DirectorRepository directorRepository;
    private final FilmGenreRepository filmGenreRepository;
    private final DirectorFilmRepository directorFilmRepository;
    private final RecommendationRepository recommendationRepository;
    private final RatingMpaRepository ratingMpaRepository;
    private final EventService eventService;
    private final UserFilmMarkRepository userFilmMarkRepository;

    @Transactional
    public Film createFilm(Film film) {
        film.setRate(0.0);
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
        List<RatingMPA> ratingsMpa = ratingMpaRepository.findAll();

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
            film.setRatingMPA(ratingsMpa.parallelStream()
                    .filter(r -> Objects.equals(r.getId(), film.getRatingMPA().getId()))
                    .findFirst().orElse(film.getRatingMPA()));
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
                    f.setUsersMarks(newFilm.getUsersMarks());
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
    public Film addFilmMark(Long id, Long userId, Integer mark) {
        Film film = filmRepository.findById(id).orElseThrow(() -> new FilmNotFoundException(id));
        if (film.getUsersMarks().stream()
                .map(UserFilmMark::getUser)
                .map(User::getId)
                .anyMatch(i -> i.equals(userId))) {
            eventService.createEvent(userId, EventType.LIKE, Operation.ADD, id);
            return film;
        }
        film.addUserMark(
                userFilmMarkRepository.save(
                        UserFilmMark.builder()
                                .film(film)
                                .user(userService.getUserById(userId))
                                .mark(mark)
                                .build()));
        eventService.createEvent(userId, EventType.LIKE, Operation.ADD, id);
        return film;
    }

    @Transactional
    public void removeMarkFilm(Long id, Long userId) {
        Film film = filmRepository.findById(id).orElseThrow(() -> new FilmNotFoundException(id));
        UserFilmMark mark = userFilmMarkRepository.findByUserIdAndFilmId(userId, id)
                .orElseThrow(() -> new MarkNotFoundException(userId, id));
        film.removeUserMark(mark);
        if (film.getUsersMarks().isEmpty()) {
            film.setRate(0d);
        }
        userFilmMarkRepository.deleteById(mark.getId());
        eventService.createEvent(userId, EventType.LIKE, Operation.REMOVE, id);
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

    public List<Film> getPopularFilms(Optional<Integer> rawCount, Optional<Integer> rawGenreID, Optional<Integer> rawYear) {
        List<Film> films;
        Integer count = rawCount.orElse(10);
        if (rawGenreID.isPresent() && rawYear.isPresent()) {
            films = filmRepository.findPopularFilmsByRateWithGenreAndYear(count, rawGenreID.get(), rawYear.get());
        } else if (rawGenreID.isPresent()) {
            films = filmRepository.findPopularFilmsByRateWithGenre(count, rawGenreID.get());
        } else if (rawYear.isPresent()) {
            films = filmRepository.findPopularFilmsByRateWithYear(count, rawYear.get());
        } else {
            films = filmRepository.findPopularFilmsByRate(count);
        }
        this.addGenresDirectorsInFilms(films);
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

    public List<Film> searchFilms(String query, List<String> byFields) {
        boolean byFilmName = byFields.contains("title");
        boolean byDirectorName = byFields.contains("director");
        if (!byFilmName && !byDirectorName) {
            log.error("Invalid parameter byFields: " + byFields);
            throw new IllegalArgumentException("Invalid parameter byFields: " + byFields);
        }
        List<Film> films = new ArrayList<>();
        if (byFilmName) {
            films.addAll(filmRepository.searchFilmsByName(query));
        }
        if (byDirectorName) {
            films.addAll(filmRepository.searchFilmsByDirectorName(query));
        }
        films = films.stream().distinct().collect(Collectors.toList());
        this.addGenresDirectorsInFilms(films);
        films.sort(this::filmComparator);
        return films;
    }

    private int filmComparator(Film film1, Film film2) {
        int result = Double.compare(film2.getRate(), film1.getRate());
        if (result == 0) {
            return Long.compare(film2.getId(), film1.getId());
        }
        return result;
    }

    public List<Film> findFilmsByIds(List<Long> filmIds) {
        List<Film> films = filmRepository.findFilmsByIds(filmIds);
        addGenresDirectorsInFilms(films);
        return films;
    }

    public List<Film> getRecommendationsForUser(Long userID) {
        return this.findFilmsByIds(recommendationRepository.findRecommendationsByUser(userID));
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return filmRepository.findCommonFilms(userId, friendId);
    }
}
