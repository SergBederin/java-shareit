package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDto userDtoGateway) {
        log.info("Выполняется запрос Post /users  для добавления пользователя {}", userDtoGateway);
        return userClient.addUser(userDtoGateway);
    }

    @PatchMapping("/{id}")
    @Validated(Marker.OnUpdate.class)
    public ResponseEntity<Object> updateUser(@PathVariable Long id,
                                             @RequestBody UserDto userDtoGateway) {
        log.info("Выполняется запрос Patch /users/{id}  для обнавления пользователя {}", userDtoGateway);
        return userClient.upUser(id, userDtoGateway);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        log.info("Выполняется запрос Delete/users/{id}  для удаления пользователя с id = {}", id);
        return userClient.rmUser(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        log.info("Выполняется запрос GET/users/{id} на получение пользователя с id = {} ", id);
        return userClient.getUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Выполняется запрос GET/users на получение всех пользователей");
        return userClient.getUsers();
    }
}