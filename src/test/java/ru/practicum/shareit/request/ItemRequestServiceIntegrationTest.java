package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "spring.datasource.username=postgres",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {
    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    void getRequestAllTest() {
        User user = User.builder().id(1L).name("User").email("user@user.ru").build();
        User owner = User.builder().id(2L).name("Owner").email("owner@user.ru").build();

        UserDto userDto = UserDto.builder().id(1L).name("User").email("user@user.ru").build();
        UserDto userOwnerDto = UserDto.builder().id(2L).name("Owner").email("owner@user.ru").build();

        LocalDateTime start = LocalDateTime.now();

        ItemDto itemDto = ItemDto.builder().name("Item").description("Item items").available(true).requestId(null).build();
        List<ItemDto> listItemDto = List.of(ItemDto.builder().id(1L).name("Item").description("Item items").available(true).requestId(null).build());

        ItemRequestDto itemRequestDto = ItemRequestDto.builder().id(1L).description("Test").created(start).requestor(user.getId()).items(listItemDto).build();

        userService.add(userDto);
        userService.add(userOwnerDto);
        itemService.add(itemDto, owner.getId());
        itemRequestService.addRequest(user.getId(), itemRequestDto);

        TypedQuery<ItemRequest> query = em.createQuery("Select u from ItemRequest u where u.requestor.id = :userId", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("userId", user.getId()).getSingleResult();

        MatcherAssert.assertThat(itemRequest.getId(), notNullValue());
        MatcherAssert.assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        MatcherAssert.assertThat(itemRequest.getCreated(), equalTo(itemRequestDto.getCreated()));
        MatcherAssert.assertThat(itemRequest.getRequestor().getId(), equalTo(itemRequestDto.getRequestor()));
    }

}
