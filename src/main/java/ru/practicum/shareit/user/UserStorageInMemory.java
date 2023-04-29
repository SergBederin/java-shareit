package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
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

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserStorageInMemory implements UserStorage {
    private final Map<Long, User> storageUser = new HashMap<>();
    private Long idUser = 0L;

    private Long getNewId() {
        return ++idUser;
    }

    @Override
    public List<User> getUsers() {
        List<User> userList = new ArrayList<>(storageUser.values());
        log.debug("Текущее количество пользователей: {}", storageUser.size());
        return userList;
    }

    @Override
    public User createUser(User user) {
        validateEmail(user);
        user.setId(getNewId());
        storageUser.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @Override
    public User updateUser(long id, User user) {
        user.setId(id);
        if (user.getEmail() != null) {
            validateEmail(user);
            storageUser.get(id).setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            storageUser.get(id).setName(user.getName());
        }
        log.info("Обновлен пользователь {}", storageUser.get(id));

        return storageUser.get(id);
    }

    @Override
    public User getUserById(Long userId) {
        if (!storageUser.containsKey(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID=" + userId + " не найден!");
        }
        log.info("Запрошен пользователь /id={}/", userId);
        return storageUser.get(userId);
    }

    @Override
    public void deleteUser(Long userId) {
        if (userId == null) {
            throw new ValidationException("Передан пустой аргумент!");
        }
        if (!storageUser.containsKey(userId)) {
            throw new NotFoundException("Пользователь с ID=" + userId + " не найден!");
        }
        storageUser.remove(userId);
    }

    private void validateEmail(User user) {

        for (User userInStorage : storageUser.values()) {
            if (user.getEmail().equals(userInStorage.getEmail()) && user.getId() != userInStorage.getId()) {
                log.info("Дублируются Email пользователей {}", user.getEmail());
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
        }

    }
}

