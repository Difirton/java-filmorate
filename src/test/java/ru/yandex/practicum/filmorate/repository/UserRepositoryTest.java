package ru.yandex.practicum.filmorate.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.entity.UserFriend;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private UserFriend user1friend2;
    private UserFriend user1Friend3;
    private UserFriend user3Friend4;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserFriendRepository userFriendRepository;

    @BeforeEach
    void setUp() {
        user1 = User.builder().id(1L).email("example1@gmail.ru").login("231ffdsf32").name("Test1 Test1")
                .birthday(LocalDate.of(1990, 5, 1)).build();
        user2 = User.builder().id(2L).email("mail2@mail.ru").login("FFad23fdas").name("Test2 Tes2")
                .birthday(LocalDate.of(1981, 11, 15)).build();
        user3 = User.builder().id(3L).email("example3@mail.com").login("648448fafaf22").name("Test3 Tes3")
                .birthday(LocalDate.of(1995, 8, 20)).build();
        user4 = User.builder().id(4L).email("example4@yandex.com").login("64GGfafaf22").name("Test4 Tes4")
                .birthday(LocalDate.of(1965, 9, 23)).build();
        user1friend2 = UserFriend.builder().user(user1).friend(user2).approved(true).build();
        user1Friend3 = UserFriend.builder().user(user1).friend(user3).approved(true).build();
        user3Friend4 = UserFriend.builder().user(user4).friend(user3).approved(true).build();
    }

    @Test
    @DisplayName("Test CRUD of UserRepository, expected ok")
    void testCreateReadDeleteUserRepository() {
        userRepository.save(user1);
        Iterable<User> usersBeforeUpdate = userRepository.findAll();
        Assertions.assertThat(usersBeforeUpdate).extracting(User::getLogin).containsOnly("231ffdsf32");
        User userToUpdate = userRepository.findById(1L).get();
        userToUpdate.setName("updated");
        userRepository.update(userToUpdate);
        User userAfterUpdate = userRepository.findById(1L).get();
        Assertions.assertThat(userAfterUpdate).extracting(User::getName).isEqualTo("updated");
    }

    @Test
    @DisplayName("Test find all friends of user")
    void testFindAllFriendsUser() {
        userRepository.saveAll(List.of(user1, user2, user3));
        user1.addFriend(user2);
        user2.addFriend(user1);
        user1.addFriend(user3);
        user3.addFriend(user1);
        userFriendRepository.save(user1, user2);
        userFriendRepository.save(user1, user3);
        List<User> friendsOfUser1 = userRepository.findAllFriendsUser(1L);
        Assertions.assertThat(friendsOfUser1).isEqualTo(List.of(user2, user3));
    }

    @Test
    @DisplayName("Test find all common friends of two users")
    void testFindCommonUsersFriends() {
        userRepository.saveAll(List.of(user1, user2, user3, user4));
        userFriendRepository.saveAll(List.of(user1friend2, user1Friend3));
        userFriendRepository.save(user3Friend4);
        List<User> commonFriendsOfUsers1and2 = userRepository.findCommonUsersFriends(1L, 4L);
        Assertions.assertThat(commonFriendsOfUsers1and2).isEqualTo(List.of(user3));
    }

    @Test
    @DisplayName("Test absent common friends of two users, expected throw IndexOutOfBoundsException")
    void testAbsenceOfCommonUsersFriends() {
        userRepository.saveAll(List.of(user1, user2, user3, user4));
        userFriendRepository.save(user1, user2);
        userFriendRepository.save(user1, user3);
        userFriendRepository.save(user1, user4);
        List<User> emptyCommonFriendsOfUsers1and2 = userRepository.findCommonUsersFriends(1L, 4L);
        assertThrows(IndexOutOfBoundsException.class, () -> emptyCommonFriendsOfUsers1and2.get(0));
    }

    @Test
    @DisplayName("Test absent common friends of two users, expected throw IndexOutOfBoundsException")
    void testDeleteFriend() {
        userRepository.saveAll(List.of(user1, user2, user3));
        userFriendRepository.save(user1, user2, true);
        userFriendRepository.save(user1, user3, true);
        userFriendRepository.delete(user1friend2);
        List<User> user1FriendsAfterDelete = userRepository.findAllFriendsUser(1L);
        Assertions.assertThat(user1FriendsAfterDelete).isEqualTo(List.of(user3));
    }
}