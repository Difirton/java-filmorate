package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.error.FilmNotFoundException;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class FilmServiceTest {
    Film film;

    @MockBean
    private FilmRepository mockRepository;

    @Autowired
    private FilmService filmService;

    @BeforeEach
    public void setUp() {
        film = Film.builder()
                .id(1L)
                .name("name film 1")
                .description("description film 1")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .build();
        when(mockRepository.findById(1L)).thenReturn(Optional.of(film));
    }

    @Test
    @DisplayName("Update all fields Film, expected ok")
    public void testUpdateAllFieldsFilm() {
        Film updatedFilm = Film.builder()
                .name("updated film")
                .description("updated film Desc")
                .releaseDate(LocalDate.of(2000, 10, 5))
                .duration(300)
                .build();
        when(mockRepository.save(any(Film.class))).thenReturn(film);
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
    public void testThrowNotFoundFilmWhenUpdate() {
        assertThrows(FilmNotFoundException.class, () -> filmService.updateFilm(2L, film));
    }

    @Test
    @DisplayName("Test throw not found film exception, when get not exist film")
    public void testThrowNotFoundFilmWhenGet() {
        assertThrows(FilmNotFoundException.class, () -> filmService.getFilmById(2L));
    }
}