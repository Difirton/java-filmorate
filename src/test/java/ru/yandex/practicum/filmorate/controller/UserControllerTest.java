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
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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
class UserControllerTest {
    private static final ObjectMapper jsonMapper = JsonMapper.builder().findAndAddModules().build();
    private User user;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService mockService;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .email("mail@mail.ru")
                .login("dolore")
                .name("Nick Name")
                .birthday(LocalDate.of(1981, 11, 15))
                .build();
        when(mockService.getUserById(1L)).thenReturn(user);
    }

    @Test
    @DisplayName("Method GET /users/1, expected host answer OK")
    public void testFindUsrById_OK_200() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("mail@mail.ru")))
                .andExpect(jsonPath("$.login", is("dolore")))
                .andExpect(jsonPath("$.name", is("Nick Name")))
                .andExpect(jsonPath("$.birthday", is("1981-11-15")));
        verify(mockService, times(1)).getUserById(1L);
    }

    @Test
    @DisplayName("Method POST /users, expected host answer CREATED")
    public void testPostNewUser_CREATED_201() throws Exception {
        User newUser = User.builder()
                .id(2L)
                .email("222mail@mail.ru")
                .login("222dolore")
                .name("Second Name")
                .birthday(LocalDate.of(1991, 12, 25))
                .build();
        when(mockService.createUser(any(User.class))).thenReturn(newUser);

        mockMvc.perform(post("/users")
                        .content(jsonMapper.writeValueAsString(newUser))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.email", is("222mail@mail.ru")))
                .andExpect(jsonPath("$.login", is("222dolore")))
                .andExpect(jsonPath("$.name", is("Second Name")))
                .andExpect(jsonPath("$.birthday", is("1991-12-25")));
        verify(mockService, times(1)).createUser(any(User.class));
    }

    @Test
    @DisplayName("Method GET /users, expected host answer OK")
    public void testFindAllUsers_OK_200() throws Exception {
        List<User> users = Arrays.asList(
                User.builder()
                        .id(1L)
                        .email("mail@mail.ru")
                        .login("dolore")
                        .name("Nick Name")
                        .birthday(LocalDate.of(1981, 11, 15))
                        .build(),
                User.builder()
                        .id(2L)
                        .email("222mail@mail.ru")
                        .login("222dolore")
                        .name("Second Name")
                        .birthday(LocalDate.of(1991, 12, 25))
                        .build());
        when(mockService.getAllUsers()).thenReturn(users);
        mockMvc.perform(get("/users"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].email", is("mail@mail.ru")))
                .andExpect(jsonPath("$[0].login", is("dolore")))
                .andExpect(jsonPath("$[0].name", is("Nick Name")))
                .andExpect(jsonPath("$[0].birthday", is("1981-11-15")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].email", is("222mail@mail.ru")))
                .andExpect(jsonPath("$[1].login", is("222dolore")))
                .andExpect(jsonPath("$[1].name", is("Second Name")))
                .andExpect(jsonPath("$[1].birthday", is("1991-12-25")));
        verify(mockService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("Method PUT /users/1, expected host answer OK")
    public void testUpdateUser_OK_200() throws Exception {
        User updateUser = User.builder()
                .id(1L)
                .email("update@yandex.com")
                .login("R2D2")
                .name("Serious Sam")
                .birthday(LocalDate.of(1985, 4, 20))
                .build();
        when(mockService.updateUser(1L, updateUser)).thenReturn(updateUser);

        mockMvc.perform(put("/users/1")
                        .content(jsonMapper.writeValueAsString(updateUser))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("update@yandex.com")))
                .andExpect(jsonPath("$.login", is("R2D2")))
                .andExpect(jsonPath("$.name", is("Serious Sam")))
                .andExpect(jsonPath("$.birthday", is("1985-04-20")));
    }

    @Test
    @DisplayName("Method DELETE /users/1, expected host answer OK")
    public void testDeleteUser_OK_200() throws Exception {
        doNothing().when(mockService).removeUserById(1L);
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        verify(mockService, times(1)).removeUserById(1L);
    }

    @Test
    @DisplayName("Method PUT /users/1/friends/2, expected host answer OK")
    public void testAddUserFriend_OK_200() throws Exception {
        User friend = User.builder()
                .id(2L)
                .email("mail@mail.ru")
                .login("dolore")
                .name("Nick Name")
                .birthday(LocalDate.of(1981, 11, 15))
                .build();
        user.addFriend(friend);
        when(mockService.addFriend(1L, 2L)).thenReturn(user);
        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Method DELETE /users/1/friends/2, expected host answer OK")
    public void testDeleteUserFriend_OK_200() throws Exception {
        User friend = User.builder()
                .id(2L)
                .email("mail@mail.ru")
                .login("dolore")
                .name("Nick Name")
                .birthday(LocalDate.of(1981, 11, 15))
                .build();
        user.addFriend(friend);
        user.removeFriend(friend);
        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());
    }
}