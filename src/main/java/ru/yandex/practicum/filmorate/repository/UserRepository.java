package ru.yandex.practicum.filmorate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.filmorate.entity.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT DISTINCT * FROM users WHERE id IN" +
            "(SELECT friend_id FROM user_friends WHERE user_id = ?1)", nativeQuery = true)
    List<User> findAllFriendsUser(Long id);

    @Query(value = "SELECT DISTINCT * FROM users WHERE id IN (SELECT friend_id FROM" +
            "(Select friend_id FROM user_friends WHERE user_id = ?1) INNER JOIN" +
            "(Select friend_id FROM user_friends WHERE user_id = ?2) USING (friend_id))", nativeQuery = true)
    List<User> findCommonUsersFriends(Long id, Long otherId);
}