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
import ru.yandex.practicum.filmorate.entity.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DirectorControllerTest {
    private static final ObjectMapper jsonMapper = JsonMapper.builder().findAndAddModules().build();
    private Director director;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DirectorService mockService;

    @BeforeEach
    void setUp() {
        director = Director.builder()
                .id(1L)
                .name("test")
                .build();
        when(mockService.getDirectorById(1L)).thenReturn(director);
    }

    @Test
    @DisplayName("Request POST /directors, expected host answer CREATED")
    void testNewDirector_CREATED_201() throws Exception {
        when(mockService.createDirector(any(Director.class))).thenReturn(director);

        mockMvc.perform(post("/directors")
                        .content(jsonMapper.writeValueAsString(director))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("test")));
        verify(mockService, times(1)).createDirector(any(Director.class));
    }

    @Test
    @DisplayName("Request GET /directors/1, expected host answer OK")
    void testFindDirector() throws Exception {
        mockMvc.perform(get("/directors/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("test")));
        verify(mockService, times(1)).getDirectorById(1L);
    }

    @Test
    @DisplayName("Request GET /directors, expected host answer OK")
    void testFindAll_OK_200() throws Exception {
        List<Director> directors = Arrays.asList(
                director,
                Director.builder()
                        .id(2L)
                        .name("test-2")
                        .build());
        when(mockService.getAllDirectors()).thenReturn(directors);
        mockMvc.perform(get("/directors"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("test")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("test-2")));
        verify(mockService, times(1)).getAllDirectors();
    }

    @Test
    @DisplayName("Request PUT /directors/1, expected host answer OK")
    void testUpdateDirector_OK_200() throws Exception {
        when(mockService.updateDirector(1L, director)).thenReturn(director);

        mockMvc.perform(put("/directors/1")
                        .content(jsonMapper.writeValueAsString(director))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("test")));
    }

    @Test
    @DisplayName("Request DELETE /directors/1, expected host answer OK")
    void testDeleteDirector_OK_200() throws Exception {
        doNothing().when(mockService).removeDirectorById(1L);
        mockMvc.perform(delete("/directors/1"))
                .andExpect(status().isOk());
        verify(mockService, times(1)).removeDirectorById(1L);
    }
}