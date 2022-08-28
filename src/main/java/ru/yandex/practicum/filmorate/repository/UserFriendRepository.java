package ru.yandex.practicum.filmorate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.filmorate.entity.UserFriend;

public interface UserFriendRepository extends JpaRepository<UserFriend, Long> {

}
