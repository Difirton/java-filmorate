package ru.yandex.practicum.filmorate.controller;

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
import ru.yandex.practicum.filmorate.entity.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

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
class GenreControllerTest {
    private static final ObjectMapper jsonMapper = JsonMapper.builder().findAndAddModules().build();
    private Genre genre;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GenreService mockService;

    @BeforeEach
    void setUp() {
        genre = Genre.builder().id(1L)
                .title("test1").build();
    }

    @Test
    @DisplayName("Request GET /genres, expected host answer OK")
    void testFindAllGenres_OK_200() throws Exception {
        Genre genre2 = Genre.builder()
                .id(2L)
                .title("test2")
                .build();
        List<Genre> genres = List.of(genre, genre2);
        when(mockService.getAllGenres()).thenReturn(genres);

        mockMvc.perform(get("/genres"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("test1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("test2")));
        verify(mockService, times(1)).getAllGenres();
    }

    @Test
    @DisplayName("Request GET /genres/1, expected host answer OK")
    void testFindGenre_OK_200() throws Exception {
        when(mockService.getGenreById(1L)).thenReturn(genre);
        mockMvc.perform(get("/genres/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("test1")));
        verify(mockService, times(1)).getGenreById(1L);
    }
}