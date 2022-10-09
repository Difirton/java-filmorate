package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.entity.Genre;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class GenreServiceTest {
    private Genre genre1;
    private Genre genre2;
    private Genre genre3;
    @MockBean
    private GenreRepository mockRepository;
    @Autowired
    private GenreService genreService;

    @BeforeEach
    void setUp() {
        genre1 = Genre.builder()
                .id(1L)
                .title("T1")
                .build();
        genre2 = Genre.builder()
                .id(2L)
                .title("T2")
                .build();
        genre3 = Genre.builder()
                .id(3L)
                .title("T3")
                .build();
    }

    @Test
    @DisplayName("Get all genres, expected ok")
    void testGetAllGenres() {
        when(mockRepository.findAll()).thenReturn(List.of(genre1, genre2, genre3));
        assertEquals(List.of(genre1, genre2, genre3), genreService.getAllGenres());
    }

    @Test
    @DisplayName("Get genre by id, expected ok")
    void testGetGenreById() {
        when(mockRepository.findById(2L)).thenReturn(Optional.of(genre2));
        assertEquals(genre2, genreService.getGenreById(2L));
    }
}