package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.entity.UserFriend;
import ru.yandex.practicum.filmorate.error.UserNotFoundException;
import ru.yandex.practicum.filmorate.repository.UserFriendRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;

@Service
public class UserService {
    private UserRepository userRepository;
    private UserFriendRepository userFriendRepository;

    @Autowired
    public UserService(UserRepository userRepository, UserFriendRepository userFriendRepository) {
        this.userRepository = userRepository;
        this.userFriendRepository = userFriendRepository;
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User newUser) {
        return userRepository.findById(id)
                .map(u -> {
                    u.setEmail(newUser.getEmail());
                    u.setLogin(newUser.getLogin());
                    if (newUser.getName().isEmpty()) {
                        u.setName(newUser.getLogin());
                    } else {
                        u.setName(newUser.getName());
                    }
                    u.setBirthday(newUser.getBirthday());
                    return userRepository.save(u);
                })
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public void removeUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public User addFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        User friend = userRepository.findById(friendId).orElseThrow(() -> new UserNotFoundException(friendId));
        UserFriend userFriend = UserFriend.builder()
                .user(user)
                .friend(friend)
                .approved(true).build();
        UserFriend friendUser = UserFriend.builder()
                .user(friend)
                .friend(user)
                .approved(true).build();
        userFriendRepository.saveAll(List.of(userFriend, friendUser));
        friend.addFriend(user);
        user.addFriend(friend);
        return user;
    }

    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        User friend = userRepository.findById(friendId).orElseThrow(() -> new UserNotFoundException(friendId));
        user.removeFriend(friend);
        friend.removeFriend(user);
        userRepository.saveAll(List.of(user, friend));
    }

    public List<User> getUserFriends(Long id) {
        return userRepository.findAllFriendsUser(id);
    }

    public List<User> getCommonUsersFriends(Long id, Long otherId) {
        return userRepository.findCommonUsersFriends(id, otherId);
    }
}
