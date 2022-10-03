package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.entity.Director;
import ru.yandex.practicum.filmorate.error.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.repository.DirectorRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class DirectorServiceTest {
    private Director director1;
    private Director director2;
    private Director director3;

    @Mock
    DirectorRepository mockRepository;

    @InjectMocks
    DirectorService directorService;

    @BeforeEach
    void setUp() {
        director1 = Director.builder()
                .id(1L)
                .name("director1")
                .build();
        director2 = Director.builder()
                .id(2L)
                .name("director2")
                .build();
        director3 = Director.builder()
                .id(3L)
                .name("director3")
                .build();
        when(mockRepository.findById(1L)).thenReturn(Optional.of(director1));
    }

    @Test
    @DisplayName("Test create new Director")
    void testCreateDirector() {
        when(mockRepository.save(director1)).thenReturn(director1);
        assertEquals(directorService.createDirector(director1), director1);
    }

    @Test
    @DisplayName("Get director by id, expected ok")
    void testGetDirectorById() {
        assertEquals(directorService.getDirectorById(1L), director1);
    }

    @Test
    @DisplayName("Get all directors, expected ok")
    void testGetAllDirectors() {
        when(mockRepository.findAll()).thenReturn(List.of(director1, director2, director3));
        assertEquals(directorService.getAllDirectors(), List.of(director1, director2, director3));
    }

    @Test
    @DisplayName("Update all fields director, expected ok")
    void testUpdateDirector() {
        Director updatedDirector = Director.builder()
                .id(1L)
                .name("updated")
                .build();
        when(mockRepository.update(any(Director.class))).thenReturn(director1);
        Director directorAfterUpdate = directorService.updateDirector(1L, updatedDirector);
        assertEquals(directorAfterUpdate.getName(), "updated");
    }

    @Test
    void removeFilmById() {
        directorService.removeDirectorById(1L);
        verify(mockRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test throw not found director exception, when update not exist director")
    void testThrowNotFoundDirectorWhenUpdate() {
        assertThrows(DirectorNotFoundException.class, () -> directorService.updateDirector(2L, director2));
    }

    @Test
    @DisplayName("Test throw not found director exception, when get not exist director")
    void testThrowNotFoundDirectorWhenGet() {
        assertThrows(DirectorNotFoundException.class, () -> directorService.getDirectorById(2L));
    }
}