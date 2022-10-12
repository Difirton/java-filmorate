package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.entity.RatingMPA;
import ru.yandex.practicum.filmorate.repository.RatingMpaRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class RatingMpaServiceTest {
    RatingMPA ratingMPA1;
    RatingMPA ratingMPA2;
    RatingMPA ratingMPA3;
    @MockBean
    private RatingMpaRepository mockRepository;
    @Autowired
    private RatingMpaService ratingMpaService;

    @BeforeEach
    void setUp() {
        ratingMPA1 = RatingMPA.builder()
                .id(1L)
                .title("T1")
                .build();
        ratingMPA2 = RatingMPA.builder()
                .id(2L)
                .title("T2")
                .build();
        ratingMPA3 = RatingMPA.builder()
                .id(3L)
                .title("T3")
                .build();
    }

    @Test
    @DisplayName("Get all ratings MPA, expected ok")
    void testGetAllRatingsMpa() {
        when(mockRepository.findAll()).thenReturn(List.of(ratingMPA1, ratingMPA2, ratingMPA3));
        assertEquals(List.of(ratingMPA1, ratingMPA2, ratingMPA3), ratingMpaService.getAllRatingsMpa());
    }

    @Test
    @DisplayName("Get rating MPA by id, expected ok")
    void testGetRatingsMpaById() {
        when(mockRepository.findById(2L)).thenReturn(Optional.of(ratingMPA2));
        assertEquals(ratingMPA2, ratingMpaService.getRatingsMpaById(2L));
    }
}