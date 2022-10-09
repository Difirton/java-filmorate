package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.error.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.repository.UserFriendRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {
    private User user;
    @MockBean
    private UserRepository mockRepository;
    @MockBean
    private UserFriendRepository mockUserFriendRepository;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        user = User.builder()
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
    void testCreateBlankNameUser() {
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

    @Test
    @DisplayName("Update all fields User, expected ok")
    void testUpdateAllFieldsUser() {
        User updatedUser = User.builder()
                .email("22mail@mail.ru")
                .login("R2D2")
                .name("Serious Sam")
                .birthday(LocalDate.of(1981, 5, 10))
                .build();
        when(mockRepository.update(any(User.class))).thenReturn(user);
        User userAfterUpdate = userService.updateUser(1L, updatedUser);
        String actualEmail = userAfterUpdate.getEmail();
        String expectedEmail = "22mail@mail.ru";
        String actualLogin = userAfterUpdate.getLogin();
        String expectedLogin = "R2D2";
        String actualName = userAfterUpdate.getName();
        String expectedName = "Serious Sam";
        LocalDate actualBirthday = userAfterUpdate.getBirthday();
        LocalDate expectedBirthday = LocalDate.of(1981, 5, 10);
        assertEquals(expectedEmail, actualEmail);
        assertEquals(expectedLogin, actualLogin);
        assertEquals(expectedName, actualName);
        assertEquals(expectedBirthday, actualBirthday);
    }

    @Test
    @DisplayName("Test throw not found user exception, when update not exist user")
    void testThrowNotFoundUserWhenUpdate() {
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(2L, user));
    }

    @Test
    @DisplayName("Test throw not found user exception, when get not exist user")
    void testThrowNotFoundUserWhenGet() {
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(2L));
    }

    @Test
    @DisplayName("Test add new friend")
    void testAddFriend() {
        User friend = User.builder()
                .id(2L)
                .login("test")
                .email("test@mail.ru")
                .name("test").build();
        when(mockRepository.findById(1L)).thenReturn(Optional.of(user));
        when(mockRepository.findById(2L)).thenReturn(Optional.of(friend));
        userService.addFriend(1L, 2L);
        assertEquals(user.getFriends().get(0).getFriend().getId(), 2L);
        assertEquals(user.getFriends().get(0).getFriend().getLogin(), "test");
        assertEquals(user.getFriends().get(0).getFriend().getEmail(), "test@mail.ru");
        assertEquals(user.getFriends().get(0).getFriend().getName(), "test");
    }
}