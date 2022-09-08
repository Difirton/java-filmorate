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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:sql_scripts/schema_JdbcUserFriendTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class JdbcUserFriendRepositoryImplTest {
    private UserFriend newUserFriend1To2;
    private UserFriend secondUserFriend2To1;
    private UserFriend thirdUserFriend1To3;
    private User user;
    private User friend;

    @Autowired
    private UserFriendRepository userFriendRepository;

    @BeforeEach
    void setUp() {
        newUserFriend1To2 = UserFriend.builder().user(User.builder().id(1L).build())
                .friend(User.builder().id(2L).build()).build();
        secondUserFriend2To1 = UserFriend.builder().user(User.builder().id(2L).build())
                .friend(User.builder().id(1L).build()).build();
        thirdUserFriend1To3 = UserFriend.builder().user(User.builder().id(1L).build())
                .friend(User.builder().id(3L).build()).build();
        user = User.builder().id(1L).build();
        friend = User.builder().id(2L).build();
    }

    @Test
    @DisplayName("Test save userFriend in UserFriendRepository")
    void testSave() {
        UserFriend returnedUserFriend = userFriendRepository.save(newUserFriend1To2);
        assertEquals(returnedUserFriend.getUser().getId(), 1L);
        UserFriend userFriendAfterSaveInDB = userFriendRepository.findById(1L).get();
        assertEquals(userFriendAfterSaveInDB.getFriend().getId(), 2L);
    }

    @Test
    @DisplayName("Test save two users in UserFriendRepository")
    void testSaveUserFriend() {
        UserFriend returnedUserFriend = userFriendRepository.save(user, friend);
        assertEquals(returnedUserFriend.getUser().getId(), 1L);
        UserFriend userFriendAfterSaveInDB = userFriendRepository.findById(1L).get();
        assertEquals(userFriendAfterSaveInDB.getFriend().getId(), 2L);
    }

    @Test
    @DisplayName("Test save two users and boolean in UserFriendRepository")
    void testSaveUserFriendIsApproved() {
        UserFriend returnedUserFriend = userFriendRepository.save(user, friend, true);
        assertEquals(returnedUserFriend.getUser().getId(), 1L);
        UserFriend userFriendAfterSaveInDB = userFriendRepository.findById(1L).get();
        assertEquals(userFriendAfterSaveInDB.getFriend().getId(), 2L);
    }

    @Test
    @DisplayName("Test update in UserFriendRepository")
    void testUpdate() {
        userFriendRepository.save(newUserFriend1To2);
        newUserFriend1To2 = UserFriend.builder().id(1L).user(User.builder().id(1L).build())
                .friend(User.builder().id(3L).build()).build();
        UserFriend returnedUserFriend = userFriendRepository.update(newUserFriend1To2);
        assertEquals(returnedUserFriend.getFriend().getId(), 3L);
        UserFriend userFriendAfterSaveInDB = userFriendRepository.findById(1L).get();
        System.out.println(userFriendAfterSaveInDB);
        assertEquals(userFriendAfterSaveInDB.getFriend().getId(), 3L);
    }

    @Test
    @DisplayName("Test delete by id in UserFriendRepository")
    void testDeleteById() {
        userFriendRepository.save(newUserFriend1To2);
        assertEquals(userFriendRepository.findAll().size(), 1);
        userFriendRepository.deleteById(1L);
        assertEquals(userFriendRepository.findAll().size(), 0);
    }

    @Test
    @DisplayName("Test delete in UserFriendRepository")
    void testDelete() {
        userFriendRepository.save(newUserFriend1To2);
        assertEquals(userFriendRepository.findAll().size(), 1);
        userFriendRepository.delete(newUserFriend1To2);
        assertEquals(userFriendRepository.findAll().size(), 0);
    }

    @Test
    @DisplayName("Test find all in UserFriendRepository")
    void testFindAll() {
        userFriendRepository.save(newUserFriend1To2);
        assertEquals(userFriendRepository.findAll().size(), 1);
        userFriendRepository.save(secondUserFriend2To1);
        assertEquals(userFriendRepository.findAll().size(), 2);
    }

    @Test
    @DisplayName("Test find by id in UserFriendRepository")
    void testFindById() {
        userFriendRepository.save(newUserFriend1To2);
        userFriendRepository.save(secondUserFriend2To1);
        userFriendRepository.save(thirdUserFriend1To3);
        assertEquals(userFriendRepository.findById(2L).get().getFriend().getId(), 1L);
    }

    @Test
    @DisplayName("Test save all in UserFriendRepository")
    void testSaveAll() {
        userFriendRepository.saveAll(List.of(newUserFriend1To2, secondUserFriend2To1, thirdUserFriend1To3));
        assertEquals(userFriendRepository.findAll().size(), 3);
        assertEquals(userFriendRepository.findById(2L).get().getFriend().getId(), 1L);
    }

    @Test
    @DisplayName("Test update all in UserFriendRepository")
    void testUpdateAll() {
        userFriendRepository.saveAll(List.of(newUserFriend1To2, secondUserFriend2To1, thirdUserFriend1To3));
        secondUserFriend2To1.setId(2L);
        secondUserFriend2To1.setUser(User.builder().id(3L).build());
        secondUserFriend2To1.setFriend(User.builder().id(2L).build());
        userFriendRepository.updateAll(List.of(secondUserFriend2To1));
    }
}