package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Component
@Repository
@Slf4j
public class UserStorageInMemory implements UserStorage {
    private Long idUser = 0L;
    private final Map<Long, User> storage = new HashMap<>();

    private Long getNewId() {
        return ++idUser;
    }

    @Override
    public List<User> getUsers() {
        List<User> userList = new ArrayList<>(storage.values());
        log.debug("Текущее количество пользователей: {}", storage.size());
        return userList;
    }

    @Override
    public User createUser(User user) {
        // user.setName(returnUserName(user));
        validateEmail(user);
        user.setId(getNewId());
        storage.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @Override
    public User updateUser(long id, User user) {
        // if (storage.get(id) != null & storage.containsKey(id)) {
            user.setId(id);
        if (user.getEmail() != null) {
            validateEmail(user);
            storage.get(id).setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            storage.get(id).setName(user.getName());
        }
        log.info("Обновлен пользователь {}", storage.get(id));

        return storage.get(id);
    }


    @Override
    public User getUserById(Long userId) {
        if (!storage.containsKey(userId)) {
            throw new NotFoundException("Пользователь с ID=" + userId + " не найден!");
        }
        return storage.get(userId);
    }

    @Override
    public void deleteUser(Long userId) {
        if (userId == null) {
            throw new ValidationException("Передан пустой аргумент!");
        }
        if (!storage.containsKey(userId)) {
            throw new NotFoundException("Пользователь с ID=" + userId + " не найден!");
        }
        storage.remove(userId);
    }

    private void validateEmail(User user) {

        for (User userInStorage : storage.values()) {
            if ( user.getEmail().equals(userInStorage.getEmail()) && user.getId() != userInStorage.getId()) {
                log.info("Дублируются Email пользователей {}", user.getEmail());
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
        }

    }
}

