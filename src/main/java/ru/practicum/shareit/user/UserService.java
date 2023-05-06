package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorageInMemory repositoryUser;
    private final UserMapper userMapper;

    public List<UserDto> getAll() {
        return repositoryUser.getUsers().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getById(Long id) {
        if (!repositoryUser.getStorageUser().containsKey(id)) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с ID=" + id + " не найден!");
        }
        return userMapper.toUserDto(repositoryUser.getUserById(id));
    }

    public UserDto add(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        if (validateEmail(user)) {
            repositoryUser.createUser(user);
        }
        return getById(user.getId());
    }

    public UserDto update(long id, UserDto userDto) {
        User user = userMapper.toUser(userDto);
        user.setId(id);
        if (validateEmail(user)) {
            repositoryUser.updateUser(id, user);
        }
        return getById(id);
    }

    public void delete(Long id) {
        if (id == null) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Передан пустой аргумент!");
        }
        if (!repositoryUser.getStorageUser().containsKey(id)) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с ID=" + id + " не найден!");
        }
        repositoryUser.deleteUser(id);
    }

    private boolean validateEmail(User user) {
        if (repositoryUser.getStorageUser().values().stream()
                .noneMatch(lastUser -> lastUser.getEmail().equals(user.getEmail())
                        && lastUser.getId() != user.getId())) {
            return true;
        } else {
            throw new ConflictException("Дублируются Email пользователей.");
        }

    }
}
