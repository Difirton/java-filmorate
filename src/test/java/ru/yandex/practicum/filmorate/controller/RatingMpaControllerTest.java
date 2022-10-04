package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.entity.RatingMPA;
import ru.yandex.practicum.filmorate.service.RatingMpaService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RatingMpaControllerTest {
    private RatingMPA ratingMPA;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RatingMpaService mockService;

    @BeforeEach
    void setUp() {
        ratingMPA = RatingMPA.builder()
                .id(1L)
                .title("G")
                .build();
    }

    @Test
    @DisplayName("Request GET /mpa, expected host answer OK")
    void testGetAllRatingsMpa_OK_200() throws Exception {
        RatingMPA ratingMPA2 = RatingMPA.builder()
                .id(2L)
                .title("R")
                .build();
        List<RatingMPA> ratings = List.of(ratingMPA, ratingMPA2);
        when(mockService.getAllRatingsMpa()).thenReturn(ratings);

        mockMvc.perform(get("/mpa"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("G")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("R")));
        verify(mockService, times(1)).getAllRatingsMpa();
    }

    @Test
    @DisplayName("Request GET /mpa/1, expected host answer OK")
    void testFindRatingMpa_OK_200() throws Exception {
        when(mockService.getRatingsMpaById(1L)).thenReturn(ratingMPA);
        mockMvc.perform(get("/mpa/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("G")));
        verify(mockService, times(1)).getRatingsMpaById(1L);
    }
}