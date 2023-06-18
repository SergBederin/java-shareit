package ru.practicum.shareit.user;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@NoArgsConstructor

public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<UserDto> getAll() {
        log.info("Запрос на получение всех пользователей выполнен");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getById(Long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден!")));
    }

    public UserDto add(UserDto userDto) {
        log.info("Добавлен пользователь = {}", userDto);
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    public UserDto update(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден!"));
        User userUpd = UserMapper.toUser(userDto);
        if (userUpd.getEmail() != null) {
            user.setEmail(userUpd.getEmail());
        }
        if (userUpd.getName() != null) {
            user.setName(userUpd.getName());
        }
        userRepository.save(user);
        log.info("Обновлен пользователь newValue={}", user);
        return getById(user.getId());
    }

    public void delete(Long userId) {
        if (userId == null) {
            throw new ValidationException("Передан пустой аргумент!");
        }
        getById(userId);
        userRepository.deleteById(userId);
        log.info("Удален пользователь id={}", userId);
    }

    /* private boolean validateEmail(User user) {
        if (!userRepository.existsByEmail(user.getEmail())) {
            return true;
        } else {
            throw new ConflictException("Дублируются Email пользователей.");
        }
    }*/

    @Transactional(readOnly = true)
    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден!"));
    }
}
