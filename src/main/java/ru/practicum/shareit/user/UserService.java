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
    private final UserStorageInMemory repository;

    public List<User> getAll() {
        return repository.getUsers();
    }

    public User getById(long id) {
        return repository.getUserById(id);
    }

    public User add(User user) {
        repository.createUser(user);
        return user;
    }

    public User update(long id, User user) {

        return repository.updateUser(id, user);
    }

    public void delete(long id) {
        User user = getById(id);
        repository.deleteUser(id);
        log.info("Пользователь с id={} удален", id);
    }
}
