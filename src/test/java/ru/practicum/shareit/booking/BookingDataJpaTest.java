package ru.practicum.shareit.booking;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
public class BookingDataJpaTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end = LocalDateTime.now().plusMinutes(1);
    User user = User.builder().name("User").email("user@user.ru").build();
    User owner = User.builder().name("Owner").email("owner@user.ru").build();
    Item item = Item.builder().owner(owner).name("Item").description("Item items").available(true).request(null).build();
    Booking booking = Booking.builder().start(start).end(end).item(item).booker(user).bookingStatus(BookingStatus.WAITING).build();

    @BeforeEach
    void dependencyInject() {
        userRepository.save(user);
        userRepository.save(owner);
        itemRepository.save(item);
        bookingRepository.save(booking);
    }

    @AfterEach
    void clearInject() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void getByIdBookingTest() {
        Booking bookingJpa = bookingRepository.getByIdBooking(booking.getId(), user.getId());

        MatcherAssert.assertThat(bookingJpa.getId(), notNullValue());
        MatcherAssert.assertThat(bookingJpa.getStart(), equalTo(booking.getStart()));
        MatcherAssert.assertThat(bookingJpa.getEnd(), equalTo(booking.getEnd()));
        MatcherAssert.assertThat(bookingJpa.getItem(), equalTo(booking.getItem()));
        MatcherAssert.assertThat(bookingJpa.getBooker(), equalTo(booking.getBooker()));
        MatcherAssert.assertThat(bookingJpa.getBookingStatus(), equalTo(booking.getBookingStatus()));
    }

    @Test
    void getBookingByBookerIdAllTest() {
        List<Booking> listBookingJpa = bookingRepository.getBookingByBookerIdAll(user.getId(), Pageable.ofSize(4));

        MatcherAssert.assertThat(listBookingJpa.get(0).getId(), notNullValue());
        MatcherAssert.assertThat(listBookingJpa.get(0).getStart(), equalTo(booking.getStart()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getEnd(), equalTo(booking.getEnd()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getItem(), equalTo(booking.getItem()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBooker(), equalTo(booking.getBooker()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBookingStatus(), equalTo(booking.getBookingStatus()));
    }

    @Test
    void getBookingByUserIdAndBookingStatusCurrentTest() {
        List<Booking> listBookingJpa = bookingRepository.getBookingByUserIdAndBookingStatusCurrent(user.getId(), start.plusSeconds(5), Pageable.ofSize(4));

        MatcherAssert.assertThat(listBookingJpa.get(0).getId(), notNullValue());
        MatcherAssert.assertThat(listBookingJpa.get(0).getStart(), equalTo(booking.getStart()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getEnd(), equalTo(booking.getEnd()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getItem(), equalTo(booking.getItem()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBooker(), equalTo(booking.getBooker()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBookingStatus(), equalTo(booking.getBookingStatus()));
    }

    @Test
    void getBookingByUserIdAndBookingStatusPastTest() {
        List<Booking> listBookingJpa = bookingRepository.getBookingByUserIdAndBookingStatusPast(user.getId(), end.plusSeconds(5), Pageable.ofSize(4));

        MatcherAssert.assertThat(listBookingJpa.get(0).getId(), notNullValue());
        MatcherAssert.assertThat(listBookingJpa.get(0).getStart(), equalTo(booking.getStart()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getEnd(), equalTo(booking.getEnd()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getItem(), equalTo(booking.getItem()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBooker(), equalTo(booking.getBooker()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBookingStatus(), equalTo(booking.getBookingStatus()));
    }

    @Test
    void getBookingByUserIdAndBookingStatusFutureTest() {
        List<Booking> listBookingJpa = bookingRepository.getBookingByUserIdAndBookingStatusFuture(user.getId(), start.minusDays(1), Pageable.ofSize(4));

        MatcherAssert.assertThat(listBookingJpa.get(0).getId(), notNullValue());
        MatcherAssert.assertThat(listBookingJpa.get(0).getStart(), equalTo(booking.getStart()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getEnd(), equalTo(booking.getEnd()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getItem(), equalTo(booking.getItem()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBooker(), equalTo(booking.getBooker()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBookingStatus(), equalTo(booking.getBookingStatus()));
    }

    @Test
    void getBookingByUserIdAndBookingStatusWaitingTest() {
        List<Booking> listBookingJpa = bookingRepository.getBookingByUserIdAndBookingStatusWaiting(user.getId(), BookingStatus.WAITING, Pageable.ofSize(4));

        MatcherAssert.assertThat(listBookingJpa.get(0).getId(), notNullValue());
        MatcherAssert.assertThat(listBookingJpa.get(0).getStart(), equalTo(booking.getStart()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getEnd(), equalTo(booking.getEnd()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getItem(), equalTo(booking.getItem()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBooker(), equalTo(booking.getBooker()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBookingStatus(), equalTo(booking.getBookingStatus()));
    }

    @Test
    void getBookingByUserIdAndBookingStatusRejectedTest() {
        Booking bookingRejected = Booking.builder().id(1L).start(start.minusDays(1)).end(end.minusDays(1)).item(item).booker(user).bookingStatus(BookingStatus.REJECTED).build();
        bookingRepository.save(bookingRejected);

        List<Booking> listBookingJpa = bookingRepository.getBookingByUserIdAndBookingStatusRejected(user.getId(), BookingStatus.REJECTED, Pageable.ofSize(4));

        MatcherAssert.assertThat(listBookingJpa.get(0).getId(), notNullValue());
        MatcherAssert.assertThat(listBookingJpa.get(0).getStart(), equalTo(bookingRejected.getStart()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getEnd(), equalTo(bookingRejected.getEnd()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getItem(), equalTo(bookingRejected.getItem()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBooker(), equalTo(bookingRejected.getBooker()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBookingStatus(), equalTo(bookingRejected.getBookingStatus()));
    }

    @Test
    void getBookingByOwnerIdAllTest() {
        List<Booking> listBookingJpa = bookingRepository.getBookingByOwnerIdAll(owner.getId(), Pageable.ofSize(4));

        MatcherAssert.assertThat(listBookingJpa.get(0).getId(), notNullValue());
        MatcherAssert.assertThat(listBookingJpa.get(0).getStart(), equalTo(booking.getStart()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getEnd(), equalTo(booking.getEnd()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getItem(), equalTo(booking.getItem()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBooker(), equalTo(booking.getBooker()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBookingStatus(), equalTo(booking.getBookingStatus()));
    }

    @Test
    void getByItemOwnerIdAndBookingStatusCurrentTest() {
        List<Booking> listBookingJpa = bookingRepository.getByItemOwnerIdAndBookingStatusCurrent(owner.getId(), start.plusSeconds(5), Pageable.ofSize(4));

        MatcherAssert.assertThat(listBookingJpa.get(0).getId(), notNullValue());
        MatcherAssert.assertThat(listBookingJpa.get(0).getStart(), equalTo(booking.getStart()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getEnd(), equalTo(booking.getEnd()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getItem(), equalTo(booking.getItem()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBooker(), equalTo(booking.getBooker()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBookingStatus(), equalTo(booking.getBookingStatus()));
    }

    @Test
    void getByItemOwnerIdAndBookingStatusPastTest() {
        List<Booking> listBookingJpa = bookingRepository.getByItemOwnerIdAndBookingStatusPast(owner.getId(), start.plusDays(1), Pageable.ofSize(4));

        MatcherAssert.assertThat(listBookingJpa.get(0).getId(), notNullValue());
        MatcherAssert.assertThat(listBookingJpa.get(0).getStart(), equalTo(booking.getStart()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getEnd(), equalTo(booking.getEnd()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getItem(), equalTo(booking.getItem()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBooker(), equalTo(booking.getBooker()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBookingStatus(), equalTo(booking.getBookingStatus()));
    }

    @Test
    void getBookingByItemOwnerIdAndBookingStatusFutureTest() {
        List<Booking> listBookingJpa = bookingRepository.getBookingByItemOwnerIdAndBookingStatusFuture(owner.getId(), start.minusDays(1), Pageable.ofSize(4));

        MatcherAssert.assertThat(listBookingJpa.get(0).getId(), notNullValue());
        MatcherAssert.assertThat(listBookingJpa.get(0).getStart(), equalTo(booking.getStart()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getEnd(), equalTo(booking.getEnd()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getItem(), equalTo(booking.getItem()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBooker(), equalTo(booking.getBooker()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBookingStatus(), equalTo(booking.getBookingStatus()));
    }

    @Test
    void getBookingByItemOwnerIdAndBookingStatusWaitingTest() {
        List<Booking> listBookingJpa = bookingRepository.getBookingByItemOwnerIdAndBookingStatusWaiting(owner.getId(), BookingStatus.WAITING, Pageable.ofSize(4));

        MatcherAssert.assertThat(listBookingJpa.get(0).getId(), notNullValue());
        MatcherAssert.assertThat(listBookingJpa.get(0).getStart(), equalTo(booking.getStart()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getEnd(), equalTo(booking.getEnd()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getItem(), equalTo(booking.getItem()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBooker(), equalTo(booking.getBooker()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBookingStatus(), equalTo(booking.getBookingStatus()));
    }

    @Test
    void getBookingByItemOwnerIdAndBookingStatusRejectedTest() {
        Booking bookingRejected = Booking.builder().id(1L).start(start.minusDays(1)).end(end.minusDays(1)).item(item).booker(user).bookingStatus(BookingStatus.REJECTED).build();
        bookingRepository.save(bookingRejected);

        List<Booking> listBookingJpa = bookingRepository.getBookingByItemOwnerIdAndBookingStatusWaiting(owner.getId(), BookingStatus.REJECTED, Pageable.ofSize(4));

        MatcherAssert.assertThat(listBookingJpa.get(0).getId(), notNullValue());
        MatcherAssert.assertThat(listBookingJpa.get(0).getStart(), equalTo(bookingRejected.getStart()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getEnd(), equalTo(bookingRejected.getEnd()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getItem(), equalTo(bookingRejected.getItem()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBooker(), equalTo(bookingRejected.getBooker()));
        MatcherAssert.assertThat(listBookingJpa.get(0).getBookingStatus(), equalTo(bookingRejected.getBookingStatus()));
    }

    @Test
    void findByItemIdLastTest() {
        Booking bookingApproved = Booking.builder().id(1L).start(start.minusDays(1)).end(end.minusDays(1)).item(item).booker(user).bookingStatus(BookingStatus.APPROVED).build();
        bookingRepository.save(bookingApproved);

        Booking bookingJpa = bookingRepository.findByItemIdLast(owner.getId(), item.getId(), start.plusDays(2));

        MatcherAssert.assertThat(bookingJpa.getId(), notNullValue());
        MatcherAssert.assertThat(bookingJpa.getStart(), equalTo(bookingApproved.getStart()));
        MatcherAssert.assertThat(bookingJpa.getEnd(), equalTo(bookingApproved.getEnd()));
        MatcherAssert.assertThat(bookingJpa.getItem(), equalTo(bookingApproved.getItem()));
        MatcherAssert.assertThat(bookingJpa.getBooker(), equalTo(bookingApproved.getBooker()));
        MatcherAssert.assertThat(bookingJpa.getBookingStatus(), equalTo(bookingApproved.getBookingStatus()));
    }

    @Test
    void findByItemIdNextTest() {
        Booking bookingApproved = Booking.builder().id(1L).start(start.minusDays(1)).end(end.minusDays(1)).item(item).booker(user).bookingStatus(BookingStatus.APPROVED).build();
        bookingRepository.save(bookingApproved);

        Booking bookingJpa = bookingRepository.findByItemIdNext(owner.getId(), item.getId(), start.minusDays(2));

        MatcherAssert.assertThat(bookingJpa.getId(), notNullValue());
        MatcherAssert.assertThat(bookingJpa.getStart(), equalTo(bookingApproved.getStart()));
        MatcherAssert.assertThat(bookingJpa.getEnd(), equalTo(bookingApproved.getEnd()));
        MatcherAssert.assertThat(bookingJpa.getItem(), equalTo(bookingApproved.getItem()));
        MatcherAssert.assertThat(bookingJpa.getBooker(), equalTo(bookingApproved.getBooker()));
        MatcherAssert.assertThat(bookingJpa.getBookingStatus(), equalTo(bookingApproved.getBookingStatus()));
    }

    @Test
    void getBookingByBookerIdAndItemIdTest() {
        Booking bookingApproved = Booking.builder().id(1L).start(start).end(end).item(item).booker(user).bookingStatus(BookingStatus.APPROVED).build();
        bookingRepository.save(bookingApproved);

        Booking bookingJpa = bookingRepository.getBookingByBookerIdAndItemId(user.getId(), item.getId(), start.plusDays(2));

        MatcherAssert.assertThat(bookingJpa.getId(), notNullValue());
        MatcherAssert.assertThat(bookingJpa.getStart(), equalTo(booking.getStart()));
        MatcherAssert.assertThat(bookingJpa.getEnd(), equalTo(booking.getEnd()));
        MatcherAssert.assertThat(bookingJpa.getItem(), equalTo(booking.getItem()));
        MatcherAssert.assertThat(bookingJpa.getBooker(), equalTo(booking.getBooker()));
        MatcherAssert.assertThat(bookingJpa.getBookingStatus(), equalTo(booking.getBookingStatus()));
    }
}
