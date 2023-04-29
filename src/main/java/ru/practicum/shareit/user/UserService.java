package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorageInMemory repositoryUser;

    public List<User> getAll() {
        return repositoryUser.getUsers();
    }

    public User getById(long id) {
        return repositoryUser.getUserById(id);
    }

    public User add(User user) {
        repositoryUser.createUser(user);
        log.info("Пользователь добавлен", user);
        return user;
    }

    public User update(long id, User user) {
        log.info("Пользователь обнавлен", user);
        return repositoryUser.updateUser(id, user);
    }

    public void delete(long id) {
        User user = getById(id);
        repositoryUser.deleteUser(user.getId());
        log.info("Пользователь с id={} удален", id);
    }
}
