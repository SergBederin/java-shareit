package ru.practicum.shareit.request;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
public class ItemRequestDataJpaTest {
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;

    LocalDateTime start = LocalDateTime.of(2025, 5, 25, 15, 15, 13);

    User owner = User.builder().name("Owner").email("owner@user.ru").build();
    User ownerTwo = User.builder().name("Two").email("two@user.ru").build();
    ItemRequest itemRequest = ItemRequest.builder().id(1L).description("Test").created(start).requestor(owner).build();
    ItemRequest itemRequestTwo = ItemRequest.builder().id(1L).description("Test").created(start).requestor(ownerTwo).build();

    @BeforeEach
    void dependencyInject() {
        userRepository.save(owner);
        userRepository.save(ownerTwo);
        itemRequestRepository.save(itemRequest);
        itemRequestRepository.save(itemRequestTwo);
    }

    @AfterEach
    void clearInject() {
        userRepository.deleteAll();
    }

    @Test
    void findItemRequestByRequestorIdTest() {
        List<ItemRequest> listItemRequest = itemRequestRepository.findItemRequestByRequestor_Id(owner.getId());

        MatcherAssert.assertThat(listItemRequest.get(0).getId(), notNullValue());
        MatcherAssert.assertThat(listItemRequest.get(0).getDescription(), equalTo(itemRequest.getDescription()));
        MatcherAssert.assertThat(listItemRequest.get(0).getCreated(), equalTo(itemRequest.getCreated()));
        MatcherAssert.assertThat(listItemRequest.get(0).getRequestor(), equalTo(itemRequest.getRequestor()));
    }

    @Test
    void findItemRequestByIdNotOrderByCreatedDescTest() {
        List<ItemRequest> listItemRequest = itemRequestRepository.findItemRequestByIdNotOrderByCreatedDesc(ownerTwo.getId(), Pageable.ofSize(4));

        MatcherAssert.assertThat(listItemRequest.get(0).getId(), notNullValue());
        MatcherAssert.assertThat(listItemRequest.get(0).getDescription(), equalTo(itemRequestTwo.getDescription()));
        MatcherAssert.assertThat(listItemRequest.get(0).getCreated(), equalTo(itemRequestTwo.getCreated()));
        MatcherAssert.assertThat(listItemRequest.get(0).getRequestor(), equalTo(itemRequestTwo.getRequestor()));
    }
}
