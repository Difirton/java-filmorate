package ru.yandex.practicum.filmorate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.filmorate.entity.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT DISTINCT u FROM users WHERE u.id IN" +
            "(Select u.friends FROM user_friends WHERE addUser = ?1)", nativeQuery = true)
    List<User> findAllFriendsUser(Long id); //TODO написать тест

    @Query(value = "SELECT DISTINCT u FROM users WHERE u.id IN (SELECT u.friends FROM" +
            "(Select u.friends FROM user_friends WHERE u.id = ?1) INNER JOIN" +
            "(Select u.friends FROM user_friends WHERE u.id = ?2))", nativeQuery = true)
    List<User> getCommonUsersFriends(Long id, Long otherId); //TODO это точно надо протестировать
}