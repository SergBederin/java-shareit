package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    //static UserService userService = new UserService();
    //static UserRepository userRepository = Mockito.mock(UserRepository.class);
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;
    static User user;
    static User user1;
    static UserDto userDto;

    @BeforeEach
    public void init() {
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        user = User.builder().id(1L).name("User").email("user@user.ru").build();
        user1 = User.builder().id(1L).name("User1").email("user1@user.ru").build();
        userDto = UserDto.builder().id(1L).name("User").email("user@user.ru").build();
    }

    @Test
    public void getAllUsersTest() {
        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(user, user1));
        List<UserDto> usersResult = userService.getAll();
        Assertions.assertEquals(usersResult.size(), 2);
        Assertions.assertEquals(usersResult, List.of(user, user1).stream().map(UserMapper::toUserDto)
                .collect(Collectors.toList()));
    }

    @Test
    public void getByIdTest() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        UserDto resultUser = userService.getById(1L);
        Assertions.assertEquals(resultUser.getId(), 1);
        Assertions.assertEquals(resultUser.getName(), user.getName());
        Assertions.assertEquals(resultUser.getEmail(), user.getEmail());
    }

    @Test
    void addTest() {
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(user);
        UserDto resultUser = userService.add(userDto);
        assertEquals(userDto, resultUser);
    }

    @Test
    void updateTest() {
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(user));
        user.setId(1L);
        user.setName("UpdUser");
        user.setEmail("upd@user.com");
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(user);
        userDto.setName("UpdUser");
        userDto.setEmail("upd@user.com");
        UserDto resultUser = userService.update(1L, userDto);
        Assertions.assertEquals(resultUser.getId(), 1);
        Assertions.assertEquals(resultUser.getName(), userDto.getName());
        Assertions.assertEquals(resultUser.getEmail(), userDto.getEmail());
    }

    @Test
    void findByIdErrTest() {
        final NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.findById(10L));
        assertEquals(exception.getMessage(), "Пользователь с ID=" + 10L + " не найден!");
    }

    @Test
    void getByUserIdErrTest() {
        Mockito.when(userRepository.findById(anyLong()))
                .thenThrow(NotFoundException.class);
        final NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getById(10L));
        assertNull(exception.getMessage());
    }

    @Test
    public void deleteUserByIdTest() {
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(user));
        userService.delete(1L);
        Mockito
                .verify(userRepository, Mockito.times(1))
                .deleteById(1L);
    }
}