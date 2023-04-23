package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getUsers();

    User createUser(User user);

    User updateUser(long id,User user);

    User getUserById(Long userId);

    void deleteUser(Long userId);
}
