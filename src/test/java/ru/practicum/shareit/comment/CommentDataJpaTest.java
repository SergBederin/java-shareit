package ru.practicum.shareit.comment;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest

public class CommentDataJpaTest {
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    LocalDateTime start = LocalDateTime.now();
    User owner = User.builder().name("Owner").email("owner@user.ru").build();
    Item item = Item.builder().owner(owner).name("Item").description("Item items").available(true).request(null).build();
    Comment comment = Comment.builder().id(1L).text("text").item(item).user(owner).created(start).build();

    @BeforeEach
    void dependencyInject() {
        userRepository.save(owner);
        itemRepository.save(item);
        commentRepository.save(comment);
    }

    @AfterEach
    void clearInject() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    void findCommentByIdTest() {
        List<Comment> listComment = commentRepository.findCommentById(item.getId());

        MatcherAssert.assertThat(listComment.get(0).getId(), notNullValue());
        MatcherAssert.assertThat(listComment.get(0).getText(), equalTo(comment.getText()));
        MatcherAssert.assertThat(listComment.get(0).getItem(), equalTo(comment.getItem()));
        MatcherAssert.assertThat(listComment.get(0).getUser(), equalTo(comment.getUser()));
        MatcherAssert.assertThat(listComment.get(0).getCreated(), equalTo(comment.getCreated()));
    }
}
