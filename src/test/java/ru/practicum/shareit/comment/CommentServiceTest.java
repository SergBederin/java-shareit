package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

class CommentServiceTest {
    static CommentService commentService = new CommentService();
    static CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    static BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);

    static User user;
    static User owner;
    static Item item;
    static LocalDateTime start;
    static LocalDateTime end;
    static Booking booking;
    static CommentDto commentDto;
    static List<Comment> listComment;
    static Comment comment;

    @BeforeAll
    static void assistant() {
        ReflectionTestUtils.setField(commentService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(commentService, "commentRepository", commentRepository);

        user = User.builder().id(1L).name("User").email("user@user.ru").build();
        owner = User.builder().id(2L).name("Owner").email("owner@user.ru").build();
        item = Item.builder().id(1L).owner(owner).name("Item").description("Item items").available(true).request(null).build();

        start = LocalDateTime.now();
        end = LocalDateTime.now().plusMinutes(1);

        booking = Booking.builder().start(start).end(end).item(item).booker(user).bookingStatus(BookingStatus.WAITING).build();
        commentDto = CommentDto.builder().text("Text test").authorName(user.getName()).created(start).build();
        listComment = List.of(Comment.builder().text("Text test").item(item).user(user).created(start).build());
        comment = Comment.builder().text("Text test").item(item).user(user).created(start).build();
    }

    @Test
    void getCommentsByItemIdTest() {
        Mockito.when(commentRepository.findCommentById(anyLong()))
                .thenReturn(listComment);

        Assertions.assertEquals(commentDto, commentService.getCommentsByItemId(1L).get(0));
    }

    @Test
    void addCommentErrTest() {
        Mockito.when(bookingRepository.getBookingByBookerIdAndItemId(anyLong(), anyLong(), any()))
                .thenReturn(null);

        final ValidationException exception = assertThrows(ValidationException.class, () -> commentService.addComment(1L, 1L, commentDto));

        Assertions.assertEquals(exception.getMessage(), "Бронирования на вещь с id =" + 1L + " у пользователя с id = " + 1L + " не найдена.");
    }
}