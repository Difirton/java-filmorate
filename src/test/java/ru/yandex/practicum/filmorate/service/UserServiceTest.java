package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {
    private UserService userService;

    @MockBean
    private UserRepository mockRepository;

    @BeforeEach
    public void setUp() {
        userService = new UserService(mockRepository);
        User user = User.builder()
                .id(1L)
                .email("mail@mail.ru")
                .login("dolore")
                .name("Nick Name")
                .birthday(LocalDate.of(1981, 11, 15))
                .build();
        when(mockRepository.findById(1L)).thenReturn(Optional.of(user));
    }

    @Test
    @DisplayName("Create new User with blank name, expected that user has name is login")
    public void createBlankNameUser() {
        User newUser = User.builder()
                .id(2L)
                .email("22mail@mail.ru")
                .login("R2D2")
                .birthday(LocalDate.of(1991, 11, 15))
                .build();
        when(mockRepository.findById(2L)).thenReturn(Optional.of(newUser));
        userService.createUser(newUser);
        User userAfterPost = userService.getUserById(2L);
        String actual = userAfterPost.getName();
        String expected = "R2D2";
        assertEquals(expected, actual);
    }
//
//    @Test
//    @DisplayName("Update all fields User, expected ok")
//    public void updateAllFieldsUser() {
//        User updatedUser = User.builder()
//                .email("22mail@mail.ru")
//                .login("R2D2")
//                .name("Serious Sam")
//                .birthday(LocalDate.of(1981, 5, 10))
//                .build();
//        userService.updateUser(1L, updatedUser);
//        User userAfterPost = userService.getUserById(2L);
//        String actual = userAfterPost.getName();
//        String expected = "R2D2";
//        assertEquals(expected, actual);
//    }
}