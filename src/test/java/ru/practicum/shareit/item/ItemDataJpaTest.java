package ru.practicum.shareit.item;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest

public class ItemDataJpaTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;

    User owner = User.builder().name("Owner").email("owner@user.ru").build();
    Item item = Item.builder().owner(owner).name("Item").description("Item items").available(true).request(null).build();

    @BeforeEach
    void dependencyInject() {
        userRepository.save(owner);
        itemRepository.save(item);
    }

    @AfterEach
    void clearInject() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void searchTest() {
        List<Item> listItem = itemRepository.search("Item", Pageable.ofSize(4));

        MatcherAssert.assertThat(listItem.get(0).getId(), notNullValue());
        MatcherAssert.assertThat(listItem.get(0).getDescription(), equalTo(item.getDescription()));
        MatcherAssert.assertThat(listItem.get(0).getName(), equalTo(item.getName()));
        MatcherAssert.assertThat(listItem.get(0).getRequest(), equalTo(item.getRequest()));
        MatcherAssert.assertThat(listItem.get(0).getOwner(), equalTo(item.getOwner()));
    }
}
