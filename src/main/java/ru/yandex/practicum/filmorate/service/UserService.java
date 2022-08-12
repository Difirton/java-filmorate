package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.error.UserNotFoundException;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;

@Service
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        user.addFriend(friend);
        userRepository.save(user);
        userRepository.save(friend);
        return user;
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        User friend = userRepository.findById(friendId).orElseThrow(() -> new UserNotFoundException(friendId));
        user.removeFriend(friend);
    }

    public List<User> getUserFriends(Long id) {
        return userRepository.findAllFriendsUser(id);
    }

    public List<User> getCommonUsersFriends(Long id, Long otherId) {
        return userRepository.getCommonUsersFriends(id, otherId);
    }
}
