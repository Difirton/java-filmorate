package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.RatingMPA;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.error.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class FilmServiceTest {
    private Film film;
    @MockBean
    private FilmRepository mockRepository;
    @MockBean
    private UserService mockUserService;
    @MockBean
    private EventService mockEventService;
    @Autowired
    private FilmService filmService;

    @BeforeEach
    void setUp() {
        film = Film.builder()
                .id(1L)
                .name("name film 1")
                .description("description film 1")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .rate(4.0)
                .ratingMPA(RatingMPA.builder().id(1L).title("G").build())
                .build();
        when(mockRepository.findById(1L)).thenReturn(Optional.of(film));
    }

    @Test
    @DisplayName("Update all fields Film, expected ok")
    void testUpdateAllFieldsFilm() {
        Film updatedFilm = Film.builder()
                .name("updated film")
                .description("updated film Desc")
                .releaseDate(LocalDate.of(2000, 10, 5))
                .duration(300)
                .ratingMPA(RatingMPA.builder().id(1L).title("G").build())
                .build();
        when(mockRepository.update(any(Film.class))).thenReturn(film);
        Film filmAfterUpdate = filmService.updateFilm(1L, updatedFilm);
        String actualName = filmAfterUpdate.getName();
        String expectedName = "updated film";
        String actualDescription = filmAfterUpdate.getDescription();
        String expectedDescription = "updated film Desc";
        LocalDate actualReleaseDate = filmAfterUpdate.getReleaseDate();
        LocalDate expectedReleaseDate = LocalDate.of(2000, 10, 5);
        Integer actualDuration = filmAfterUpdate.getDuration();
        Integer expectedDuration = 300;
        assertEquals(expectedName, actualName);
        assertEquals(expectedDescription, actualDescription);
        assertEquals(expectedReleaseDate, actualReleaseDate);
        assertEquals(expectedDuration, actualDuration);
    }

    @Test
    @DisplayName("Test throw not found film exception, when update not exist film")
    void testThrowNotFoundFilmWhenUpdate() {
        assertThrows(FilmNotFoundException.class, () -> filmService.updateFilm(2L, film));
    }

    @Test
    @DisplayName("Test throw not found film exception, when get not exist film")
    void testThrowNotFoundFilmWhenGet() {
        assertThrows(FilmNotFoundException.class, () -> filmService.getFilmById(2L));
    }

    @Test
    @DisplayName("Test create new Film")
    void testCreateFilm() {
        when(mockRepository.save(film)).thenReturn(film);
        assertEquals(film, filmService.createFilm(film));
    }

    @Test
    @DisplayName("Test get all films")
    void testGetAllFilms() {
        Film film2 = Film.builder()
                .id(2L)
                .name("name film 2")
                .description("description film 2")
                .releaseDate(LocalDate.of(1999, 3, 25))
                .duration(1000)
                .ratingMPA(RatingMPA.builder().id(2L).title("T").build())
                .build();
        when(mockRepository.findAll()).thenReturn(List.of(film, film2));
        assertEquals(List.of(film, film2), filmService.getAllFilms());
    }

//    @Test
//    @DisplayName("Test add like to film")
//    void testAddLikeFilm() {
//        when(mockUserService.getUserById(1L)).thenReturn(User.builder()
//                .id(1L)
//                .login("test")
//                .email("test@mail.ru")
//                .name("test").build());
//        when(mockRepository.update(film)).thenReturn(film);
//        Film updatedFilm = filmService.addFilmMark(1L, 1L, 6);
//        assertEquals(5.0, updatedFilm.getRate());
//    }

    @Test
    @Sql(scripts = {"classpath:schema.sql", "classpath:sql_scripts/schema_searchFilms.sql"})
    @DisplayName("Test search films by incorrect filter")
    void testSearchFilmsThrowException() {
        assertThrows(IllegalArgumentException.class, () -> filmService.searchFilms("uck",
                List.of("something_bad")));
    }
}