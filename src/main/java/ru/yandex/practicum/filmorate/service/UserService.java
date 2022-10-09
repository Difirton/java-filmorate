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
    private final UserRepository userRepository;
    private final UserFriendRepository userFriendRepository;

    @Autowired
    public UserService(UserRepository userRepository, UserFriendRepository userFriendRepository) {
        this.userRepository = userRepository;
        this.userFriendRepository = userFriendRepository;
    }

    @Transactional
    public User createUser(User user) {
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
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
                    return userRepository.update(u);
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
        userFriendRepository.save(user, friend);
        user.addFriend(friend);
        return user;
    }

    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        User friend = userRepository.findById(friendId).orElseThrow(() -> new UserNotFoundException(friendId));
        userFriendRepository.delete(UserFriend.builder().user(user).friend(friend).build());
    }

    public List<User> getUserFriends(Long id) {
        userRepository.findById(id).orElseThrow(() ->new UserNotFoundException(id));
        return userRepository.findAllFriendsUser(id);
    }

    public List<User> getCommonUsersFriends(Long id, Long otherId) {
        return userRepository.findCommonUsersFriends(id, otherId);
    }
}
