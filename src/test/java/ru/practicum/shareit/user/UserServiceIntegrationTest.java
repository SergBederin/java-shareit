package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "spring.datasource.username=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {
    private final EntityManager em;
    private final UserService userService;

    @Test
    void saveUserTest() {
        UserDto userDto = UserDto.builder().id(1L).name("User").email("user@user.ru").build();
        userService.add(userDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.name like :nameUser", User.class);
        User user = query.setParameter("nameUser", userDto.getName()).getSingleResult();
        MatcherAssert.assertThat(user.getId(), notNullValue());
        MatcherAssert.assertThat(user.getName(), equalTo(userDto.getName()));
        MatcherAssert.assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }
}
