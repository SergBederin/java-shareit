package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "spring.datasource.username=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIntegrationTest {
    private final EntityManager em;
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    void addTest() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusMinutes(1);
        User user = User.builder().id(1L).name("User").email("user@user.ru").build();
        User owner = User.builder().id(2L).name("Owner").email("owner@user.ru").build();
        UserDto userDto = UserDto.builder().id(1L).name("User").email("user@user.ru").build();
        UserDto userOwnerDto = UserDto.builder().id(2L).name("Owner").email("owner@user.ru").build();
        Item item = Item.builder().id(1L).owner(owner).name("Item").description("Item items").available(true).request(null).build();
        ItemDto itemDto = ItemDto.builder().name("Item").description("Item items").available(true).requestId(null).build();
        BookingShortDto bookingShortDto = BookingShortDto.builder().start(start).end(end).itemId(item.getId()).build();
        BookingDto bookingDto = BookingDto.builder().start(start).end(end).status(BookingStatus.WAITING).booker(user).item(item).build();

        userService.add(userDto);
        userService.add(userOwnerDto);
        itemService.add(itemDto, owner.getId());
        bookingService.add(userDto.getId(), bookingShortDto);

        TypedQuery<Booking> query = em.createQuery("Select u from Booking u where u.booker.id = :bookerId", Booking.class);
        Booking booking = query.setParameter("bookerId", user.getId()).getSingleResult();

        MatcherAssert.assertThat(booking.getId(), notNullValue());
        MatcherAssert.assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
        MatcherAssert.assertThat(booking.getEnd(), equalTo(bookingDto.getEnd()));
        MatcherAssert.assertThat(booking.getBookingStatus(), equalTo(bookingDto.getStatus()));
    }

    @Test
    void bookingConfirmTest() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusMinutes(1);
        User user = User.builder().id(1L).name("User").email("user@user.ru").build();
        User owner = User.builder().id(2L).name("Owner").email("owner@user.ru").build();
        UserDto userDto = UserDto.builder().id(1L).name("User").email("user@user.ru").build();
        UserDto userOwnerDto = UserDto.builder().id(2L).name("Owner").email("owner@user.ru").build();
        Item item = Item.builder().id(1L).owner(owner).name("Item").description("Item items").available(true).request(null).build();
        ItemDto itemDto = ItemDto.builder().name("Item").description("Item items").available(true).requestId(null).build();
        BookingDto bookingDto = BookingDto.builder().start(start).end(end).status(BookingStatus.WAITING).booker(user).item(item).build();

        UserDto userDtoDb = userService.add(userDto);
        UserDto ownerDtoDb = userService.add(userOwnerDto);
        ItemDto itemDtoDb = itemService.add(itemDto, ownerDtoDb.getId());
        BookingShortDto bookingShortDto = BookingShortDto.builder().start(start).end(end).itemId(itemDtoDb.getId()).build();
        BookingDto bookingDtoDb = bookingService.add(userDtoDb.getId(), bookingShortDto);
        bookingService.bookingConfirm(ownerDtoDb.getId(), bookingDtoDb.getId(), true);

        TypedQuery<Booking> query = em.createQuery("Select u from Booking u where u.booker.id = :bookerId", Booking.class);
        Booking booking = query.setParameter("bookerId", userDtoDb.getId()).getSingleResult();

        MatcherAssert.assertThat(booking.getId(), notNullValue());
        MatcherAssert.assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
        MatcherAssert.assertThat(booking.getEnd(), equalTo(bookingDto.getEnd()));
        MatcherAssert.assertThat(booking.getBookingStatus(), equalTo(BookingStatus.APPROVED));

    }
}