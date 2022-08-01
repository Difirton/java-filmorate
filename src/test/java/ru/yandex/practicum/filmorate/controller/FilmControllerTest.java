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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FilmControllerTest {
    private static final ObjectMapper jsonMapper = JsonMapper.builder().findAndAddModules().build();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmRepository mockRepository;

    @BeforeEach
    public void setUp() {
        Film film = Film.builder()
                .id(1L)
                .name("name film 1")
                .description("description film 1")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .build();
        when(mockRepository.findById(1L)).thenReturn(Optional.of(film));
    }

    @Test
    @DisplayName("Method GET /films/1, expected host answer OK")
    public void findFilmByIdOK_200() throws Exception {
        mockMvc.perform(get("/films/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("name film 1")))
                .andExpect(jsonPath("$.description", is("description film 1")))
                .andExpect(jsonPath("$.releaseDate", is("1967-03-25")))
                .andExpect(jsonPath("$.duration", is(100)));
        verify(mockRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Method POST /films, expected host answer CREATED")
    public void postNewFilmCREATED_201() throws Exception {
        Film newFilm = Film.builder()
                .id(2L)
                .name("name film 2")
                .description("description film 2")
                .releaseDate(LocalDate.of(2000, 10, 5))
                .duration(300)
                .build();
        when(mockRepository.save(any(Film.class))).thenReturn(newFilm);

        mockMvc.perform(post("/films")
                        .content(jsonMapper.writeValueAsString(newFilm))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("name film 2")))
                .andExpect(jsonPath("$.description", is("description film 2")))
                .andExpect(jsonPath("$.releaseDate", is("2000-10-05")))
                .andExpect(jsonPath("$.duration", is(300)));
        verify(mockRepository, times(1)).save(any(Film.class));
    }

    @Test
    @DisplayName("Method GET /films, expected host answer OK")
    public void findAllFilmsOK_200() throws Exception {
        List<Film> films = Arrays.asList(
                Film.builder()
                        .id(1L)
                        .name("name film 1")
                        .description("description film 1")
                        .releaseDate(LocalDate.of(1967, 3, 25))
                        .duration(100)
                        .build(),
                Film.builder()
                        .id(2L)
                        .name("name film 2")
                        .description("description film 2")
                        .releaseDate(LocalDate.of(2000, 10, 5))
                        .duration(300)
                        .build());
        when(mockRepository.findAll()).thenReturn(films);
        mockMvc.perform(get("/films"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("name film 1")))
                .andExpect(jsonPath("$[0].description", is("description film 1")))
                .andExpect(jsonPath("$[0].releaseDate", is("1967-03-25")))
                .andExpect(jsonPath("$[0].duration", is(100)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("name film 2")))
                .andExpect(jsonPath("$[1].description", is("description film 2")))
                .andExpect(jsonPath("$[1].releaseDate", is("2000-10-05")))
                .andExpect(jsonPath("$[1].duration", is(300)));
        verify(mockRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Method PUT /films/1, expected host answer OK")
    public void updateFilmOK_200() throws Exception {
        Film updateFilm = Film.builder()
                .id(1L)
                .name("update name")
                .description("update description")
                .releaseDate(LocalDate.of(2000, 10, 5))
                .duration(300)
                .build();
        when(mockRepository.save(any(Film.class))).thenReturn(updateFilm);

        mockMvc.perform(put("/films/1")
                        .content(jsonMapper.writeValueAsString(updateFilm))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("update name")))
                .andExpect(jsonPath("$.description", is("update description")))
                .andExpect(jsonPath("$.releaseDate", is("2000-10-05")))
                .andExpect(jsonPath("$.duration", is(300)));
    }

    @Test
    @DisplayName("Method DELETE /films/1, expected host answer OK")
    public void deleteFilmOK_200() throws Exception {
        doNothing().when(mockRepository).deleteById(1L);
        mockMvc.perform(delete("/films/1"))
                .andExpect(status().isOk());
        verify(mockRepository, times(1)).deleteById(1L);
    }
}