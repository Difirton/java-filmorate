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
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        user1 = User.builder().id(1L).email("example@gmail.ru").login("231ffdsf32").name("Test1 Test1")
                .birthday(LocalDate.of(1990, 5, 1)).build();
        user2 = User.builder().id(2L).email("mail@mail.ru").login("FFad23fdas").name("Test2 Tes2")
                .birthday(LocalDate.of(1981, 11, 15)).build();
        user3 = User.builder().id(3L).email("example@mail.com").login("648448fafaf22").name("Test3 Tes3")
                .birthday(LocalDate.of(1995, 8, 20)).build();
        user4 = User.builder().id(4L).email("example@yandex.com").login("64GGfafaf22").name("Test4 Tes4")
                .birthday(LocalDate.of(1965, 9, 23)).build();
    }

    @Test
    @DisplayName("Test CRUD of UserRepository, expected ok")
    public void testCreateReadDeleteUserRepository() {
        userRepository.save(user1);
        Iterable<User> usersBeforeUpdate = userRepository.findAll();
        Assertions.assertThat(usersBeforeUpdate).extracting(User::getLogin).containsOnly("231ffdsf32");
        User userToUpdate = userRepository.findById(1L).get();
        userToUpdate.setName("updated");
        userRepository.save(userToUpdate);
        User userAfterUpdate = userRepository.findById(1L).get();
        Assertions.assertThat(userAfterUpdate).extracting(User::getName).isEqualTo("updated");
        userRepository.deleteAll();
        Assertions.assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Test SQL native query find all friends of user")
    public void testFindAllFriendsUser() {
        user1.addFriend(user2);
        user1.addFriend(user3);
        userRepository.saveAll(List.of(user1,user2, user3));
        List<User> friendsOfUser1 = userRepository.findAllFriendsUser(1L);
        Assertions.assertThat(friendsOfUser1).isEqualTo(List.of(user2, user3));
    }

    @Test
    @DisplayName("Test SQL native query find all common friends of two users")
    public void testFindCommonUsersFriends() {
        user1.addFriend(user2);
        user1.addFriend(user3);
        userRepository.saveAll(List.of(user1,user2, user3));
        user4.addFriend(user3);
        userRepository.save(user4);
        List<User> commonFriendsOfUsers1and2 = userRepository.findCommonUsersFriends(1L, 4L);
        Assertions.assertThat(commonFriendsOfUsers1and2).isEqualTo(List.of(user3));
    }

    @Test
    @DisplayName("Test absent common friends of two users, expected throw IndexOutOfBoundsException")
    public void testAbsenceOfCommonUsersFriends() {
        user1.addFriend(user2);
        user1.addFriend(user3);
        userRepository.saveAll(List.of(user1,user2, user3));
        user4.addFriend(user1);
        userRepository.save(user4);
        List<User> emptyCommonFriendsOfUsers1and2 = userRepository.findCommonUsersFriends(1L, 4L);
        assertThrows(IndexOutOfBoundsException.class, () -> emptyCommonFriendsOfUsers1and2.get(0));
    }

    @Test
    @DisplayName("Test absent common friends of two users, expected throw IndexOutOfBoundsException")
    public void testDeleteFriend() {
        user1.addFriend(user2);
        user1.addFriend(user3);
        userRepository.saveAll(List.of(user1,user2, user3));
        user1.removeFriend(user2);
        userRepository.saveAll(List.of(user1,user2));
        List<User> user1FriendsAfterDelete = userRepository.findAllFriendsUser(1L);
        Assertions.assertThat(user1FriendsAfterDelete).isEqualTo(List.of(user3));
    }
}