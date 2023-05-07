package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Getter
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
        user.setId(getNewId());
        storageUser.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @Override
    public User updateUser(long id, User user) {
        user.setId(id);
        storageUser.values().forEach(lastUser -> {
            if (lastUser.getId() == id) {
                updater(lastUser, user);
            }
        });
        log.info("Обновлен пользователь {}", storageUser.get(id));
        return getUserById(id);
    }

    @Override
    public User getUserById(Long userId) {
        log.info("Запрошен пользователь /id={}/", userId);
        return storageUser.get(userId);
    }

    @Override
    public void deleteUser(Long userId) {
        storageUser.remove(userId);
        log.info("Пользователь с id={} удален", userId);
    }

    private void updater(User lastUser, User newUser) {
        if (newUser.getName() != null) {
            lastUser.setName(newUser.getName());
        } else {
            log.info("При обновление данных пользователя неуказано имя {}", newUser.getName());
        }
        if (newUser.getEmail() != null) {
            lastUser.setEmail(newUser.getEmail());
        } else {
            log.info("При обновление данных пользователя неуказан email {}", newUser.getEmail());
        }
    }
}

