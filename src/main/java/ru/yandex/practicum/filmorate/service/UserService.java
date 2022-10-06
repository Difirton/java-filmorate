package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.*;
import ru.yandex.practicum.filmorate.error.UserNotFoundException;
import ru.yandex.practicum.filmorate.repository.EventRepository;
import ru.yandex.practicum.filmorate.repository.UserFriendRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserFriendRepository userFriendRepository;
    private final EventRepository eventRepository;

    @Autowired
    public UserService(UserRepository userRepository, UserFriendRepository userFriendRepository, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.userFriendRepository = userFriendRepository;
        this.eventRepository = eventRepository;
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
        Event event = Event.builder()
                .eventId(0L)
                .timestamp(System.currentTimeMillis())
                .userId(userId)
                .eventType(EventTypes.FRIEND)
                .operation(Operations.ADD)
                .entityId(friendId)
                .build();
        eventRepository.save(event);
        return user;
    }

    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        User friend = userRepository.findById(friendId).orElseThrow(() -> new UserNotFoundException(friendId));
        userFriendRepository.delete(UserFriend.builder().user(user).friend(friend).build());
        Event event = Event.builder()
                .eventId(0L)
                .timestamp(System.currentTimeMillis())
                .userId(userId)
                .eventType(EventTypes.FRIEND)
                .operation(Operations.REMOVE)
                .entityId(friendId)
                .build();
        eventRepository.save(event);
    }

    public List<User> getUserFriends(Long id) {
        userRepository.findById(id).orElseThrow(() ->new UserNotFoundException(id));
        return userRepository.findAllFriendsUser(id);
    }

    public List<User> getCommonUsersFriends(Long id, Long otherId) {
        return userRepository.findCommonUsersFriends(id, otherId);
    }
}
