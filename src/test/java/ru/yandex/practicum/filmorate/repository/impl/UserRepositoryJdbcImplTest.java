package ru.yandex.practicum.filmorate.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.entity.UserFriend;
import ru.yandex.practicum.filmorate.repository.UserFriendRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserRepositoryJdbcImplTest {
    private User newUser;
    private User secondUser;
    private User thirdUser;
    private User fourthUser;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserFriendRepository userFriendRepository;

    @BeforeEach
    void setUp() {
        newUser = User.builder().login("test1").email("test1@mail.ru").name("test1")
                .birthday(LocalDate.of(2000, 1, 1)).build();
        secondUser = User.builder().login("test2").email("test2@mail.ru").name("test2")
                .birthday(LocalDate.of(2010, 1, 1)).build();
        thirdUser = User.builder().login("test3").email("test3@mail.ru").name("test3")
                .birthday(LocalDate.of(1991, 1, 1)).build();
        fourthUser = User.builder().login("test4").email("test4@mail.ru").name("test4")
                .birthday(LocalDate.of(1990, 1, 1)).build();
    }

    @Test
    @DisplayName("Test save user in UserRepository")
    void testSave() {
        userRepository.save(newUser);
        assertEquals(userRepository.findById(1L).get().getLogin(), "test1");
        assertEquals(userRepository.findById(1L).get().getEmail(), "test1@mail.ru");
        assertEquals(userRepository.findById(1L).get().getName(), "test1");
        assertEquals(userRepository.findById(1L).get().getBirthday(), LocalDate.of(2000, 1, 1));
    }

    @Test
    @DisplayName("Test update user in UserRepository")
    void testUpdate() {
        userRepository.save(newUser);
        newUser.setName("updated");
        newUser.setEmail("updated@mail.ru");
        newUser.setLogin("updated");
        newUser.setBirthday(LocalDate.of(2010, 1, 1));
        userRepository.update(newUser);
        assertEquals(userRepository.findById(1L).get().getLogin(), "updated");
        assertEquals(userRepository.findById(1L).get().getEmail(), "updated@mail.ru");
        assertEquals(userRepository.findById(1L).get().getName(), "updated");
        assertEquals(userRepository.findById(1L).get().getBirthday(), LocalDate.of(2010, 1, 1));
    }

    @Test
    @DisplayName("Test delete user by id in UserRepository")
    void testDeleteById() {
        userRepository.save(newUser);
        assertEquals(userRepository.findAll().size(), 1);
        userRepository.deleteById(1L);
        assertEquals(userRepository.findAll().size(), 0);
    }

    @Test
    @DisplayName("Test find all users in UserRepository")
    void testFindAll() {
        userRepository.save(newUser);
        assertEquals(userRepository.findAll().size(), 1);
        userRepository.save(secondUser);
        assertEquals(userRepository.findAll().size(), 2);
        userRepository.save(thirdUser);
        assertEquals(userRepository.findAll().size(), 3);
        assertEquals(userRepository.findAll().get(0), newUser);
        assertEquals(userRepository.findAll().get(1), secondUser);
        assertEquals(userRepository.findAll().get(2), thirdUser);
    }

    @Test
    @DisplayName("Test find all friends users in UserRepository")
    void testFindAllFriendsUser() {
        userRepository.saveAll(List.of(newUser, secondUser, thirdUser));
        UserFriend friends1And2 = UserFriend.builder()
                .user(userRepository.findById(1L).get())
                .friend(userRepository.findById(2L).get()).build();
        UserFriend friends1And3 = UserFriend.builder()
                .user(userRepository.findById(1L).get())
                .friend(userRepository.findById(3L).get()).build();
        userFriendRepository.saveAll(List.of(friends1And2, friends1And3));
        assertEquals(userRepository.findAllFriendsUser(1L), List.of(secondUser, thirdUser));
    }

    @Test
    @DisplayName("Test find all common friends two users in UserRepository")
    void testFindCommonUsersFriends() {
        userRepository.saveAll(List.of(newUser, secondUser, thirdUser, fourthUser));
        UserFriend friends1And2 = UserFriend.builder()
                .user(userRepository.findById(1L).get())
                .friend(userRepository.findById(2L).get()).build();
        UserFriend friends1And3 = UserFriend.builder()
                .user(userRepository.findById(1L).get())
                .friend(userRepository.findById(3L).get()).build();
        UserFriend friends1And4 = UserFriend.builder()
                .user(userRepository.findById(1L).get())
                .friend(userRepository.findById(4L).get()).build();
        UserFriend friends2And4 = UserFriend.builder()
                .user(userRepository.findById(2L).get())
                .friend(userRepository.findById(4L).get()).build();
        userFriendRepository.saveAll(List.of(friends1And2, friends1And3, friends1And4, friends2And4));
        assertEquals(userRepository.findCommonUsersFriends(1L, 2L), List.of(fourthUser));
    }

    @Test
    @DisplayName("Test find all common friends two users in UserRepository")
    void testFindById() {
        userRepository.save(newUser);
        userRepository.save(secondUser);
        userRepository.save(thirdUser);
        assertEquals(userRepository.findById(1L).get(), newUser);
        assertEquals(userRepository.findById(2L).get(), secondUser);
        assertEquals(userRepository.findById(3L).get(), thirdUser);
    }

    @Test
    @DisplayName("Test find all common friends two users in UserRepository")
    void testSaveAll() {
        userRepository.saveAll(List.of(newUser, secondUser, thirdUser));
        assertEquals(userRepository.findById(1L).get(), newUser);
        assertEquals(userRepository.findById(2L).get(), secondUser);
        assertEquals(userRepository.findById(3L).get(), thirdUser);
    }

    @Test
    @DisplayName("Test update all users in UserRepository")
    void testUpdateAll() {
        userRepository.saveAll(List.of(newUser, secondUser, thirdUser));
        newUser = userRepository.findById(1L).get();
        newUser.setName("updated");
        secondUser = userRepository.findById(2L).get();
        secondUser.setLogin("updated");
        userRepository.updateAll(List.of(newUser, secondUser));
        assertEquals(userRepository.findById(1L).get(), newUser);
        assertEquals(userRepository.findById(2L).get(), secondUser);
    }
}