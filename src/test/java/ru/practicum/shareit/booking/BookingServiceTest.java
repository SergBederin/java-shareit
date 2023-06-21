package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotStateException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

class BookingServiceTest {
    static BookingService bookingService = new BookingService();
    static BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
    static UserService userService = Mockito.mock(UserService.class);
    static ItemService itemService = Mockito.mock(ItemService.class);
    static User user;
    static UserDto userDto;
    static User owner;
    static Item item;
    static ItemDto itemDto;
    static BookingDto bookingDto;
    static Booking booking;
    static LocalDateTime start;
    static LocalDateTime end;

    @BeforeAll
    static void init() {
        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(bookingService, "userService", userService);
        ReflectionTestUtils.setField(bookingService, "itemService", itemService);

        user = User.builder().id(1L).name("User").email("user@user.ru").build();
        userDto = UserDto.builder().id(1L).name("User").email("user@user.ru").build();
        owner = User.builder().id(2L).name("Owner").email("owner@user.ru").build();
        item = Item.builder().id(1L).owner(owner).name("Item").description("Item items").available(true).request(null).build();
        itemDto = ItemDto.builder().id(1L).name("Item").description("Item items").available(true).requestId(null).build();
        start = LocalDateTime.of(2020, 5, 5, 14, 25, 11);
        end = LocalDateTime.of(2025, 5, 5, 14, 27, 11);
        bookingDto = BookingDto.builder().start(start).end(end).status(BookingStatus.WAITING).booker(user).item(item).build();
        booking = Booking.builder().start(start).end(end).item(item).booker(user).bookingStatus(BookingStatus.WAITING).build();
    }

    @Test
    void addTest() {
        BookingShort bookingShort = BookingShort.builder().start(start).end(end).itemId(1L).build();

        Mockito.when(userService.findById(any()))
                .thenReturn(user);
        Mockito.when(itemService.getById(any()))
                .thenReturn(item);
        Mockito.when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        userService.add(userDto);
        itemService.add(itemDto, 1L);
        BookingDto bookingDtoResult = bookingService.add(user.getId(), bookingShort);

        Assertions.assertEquals(bookingDto, bookingDtoResult);
    }

    @Test
    void bookingConfirmTest() {
        Booking bookingApproved = Booking.builder().id(1L).start(start).end(end).item(item).booker(user).bookingStatus(BookingStatus.APPROVED).build();
        Optional<Booking> bookingOptional = Optional.of(Booking.builder().id(1L).start(start).end(end).item(item).booker(user).bookingStatus(BookingStatus.WAITING).build());
        BookingDto bookingDtoApproved = BookingDto.builder().id(1L).start(start).end(end).status(BookingStatus.APPROVED).booker(user).item(item).build();

        Mockito.when(bookingRepository.findById(1L))
                .thenReturn(bookingOptional);
        Mockito.when(bookingRepository.save(any(Booking.class)))
                .thenReturn(bookingApproved);

        Assertions.assertEquals(bookingDtoApproved, bookingService.bookingConfirm(owner.getId(), 1L, true));
    }

    @Test
    void getByIdBookingTest() {
        Optional<Booking> bookingOptional = Optional.of(Booking.builder().id(1L).start(start).end(end).item(item).booker(user).bookingStatus(BookingStatus.WAITING).build());

        Mockito.when(bookingRepository.findById(1L))
                .thenReturn(bookingOptional);
        Mockito.when(bookingRepository.getByIdBooking(1L, 1L))
                .thenReturn(booking);

        Assertions.assertEquals(bookingDto, bookingService.getByIdBooking(1L, 1L));
    }

    @Test
    void getByIdListBookingsTest() {
        List<BookingDto> listBookingDto = List.of(BookingDto.builder().start(start).end(end).status(BookingStatus.WAITING).booker(user).item(item).build());
        List<Booking> listBooking = List.of(Booking.builder().start(start).end(end).item(item).booker(user).bookingStatus(BookingStatus.WAITING).build());

        Mockito.when(bookingRepository.getBookingByBookerIdAll(1L, Pageable.ofSize(4)))
                .thenReturn(listBooking);
        Mockito.when(bookingRepository.getBookingByUserIdAndBookingStatusCurrent(anyLong(), any(), any()))
                .thenReturn(listBooking);
        Mockito.when(bookingRepository.getBookingByUserIdAndBookingStatusPast(anyLong(), any(), any()))
                .thenReturn(listBooking);
        Mockito.when(bookingRepository.getBookingByUserIdAndBookingStatusFuture(anyLong(), any(), any()))
                .thenReturn(listBooking);
        Mockito.when(bookingRepository.getBookingByUserIdAndBookingStatusWaiting(1L, BookingStatus.WAITING, Pageable.ofSize(4)))
                .thenReturn(listBooking);
        Mockito.when(bookingRepository.getBookingByUserIdAndBookingStatusRejected(1L, BookingStatus.REJECTED, Pageable.ofSize(4)))
                .thenReturn(listBooking);

        Assertions.assertEquals(listBookingDto, bookingService.getByIdListBookings(1L, "ALL", 0, 4));
        Assertions.assertEquals(listBookingDto, bookingService.getByIdListBookings(1L, "CURRENT", 0, 4));
        Assertions.assertEquals(listBookingDto, bookingService.getByIdListBookings(1L, "PAST", 0, 4));
        Assertions.assertEquals(listBookingDto, bookingService.getByIdListBookings(1L, "FUTURE", 0, 4));
        Assertions.assertEquals(listBookingDto, bookingService.getByIdListBookings(1L, "WAITING", 0, 4));
        Assertions.assertEquals(listBookingDto, bookingService.getByIdListBookings(1L, "REJECTED", 0, 4));
    }

    @Test
    void getByIdOwnerBookingItemsTest() {
        List<BookingDto> listBookingDto = List.of(BookingDto.builder().start(start).end(end).status(BookingStatus.WAITING).booker(user).item(item).build());
        List<Booking> listBooking = List.of(Booking.builder().start(start).end(end).item(item).booker(user).bookingStatus(BookingStatus.WAITING).build());

        Mockito.when(bookingRepository.getBookingByOwnerIdAll(1L, Pageable.ofSize(4)))
                .thenReturn(listBooking);
        Mockito.when(bookingRepository.getByItemOwnerIdAndBookingStatusCurrent(anyLong(), any(), any()))
                .thenReturn(listBooking);
        Mockito.when(bookingRepository.getByItemOwnerIdAndBookingStatusPast(anyLong(), any(), any()))
                .thenReturn(listBooking);
        Mockito.when(bookingRepository.getBookingByItemOwnerIdAndBookingStatusFuture(anyLong(), any(), any()))
                .thenReturn(listBooking);
        Mockito.when(bookingRepository.getBookingByItemOwnerIdAndBookingStatusWaiting(1L, BookingStatus.WAITING, Pageable.ofSize(4)))
                .thenReturn(listBooking);
        Mockito.when(bookingRepository.getBookingByItemOwnerIdAndBookingStatusRejected(1L, BookingStatus.REJECTED, Pageable.ofSize(4)))
                .thenReturn(listBooking);

        Assertions.assertEquals(listBookingDto, bookingService.getByIdOwnerBookingItems(1L, "ALL", 0, 4));
        Assertions.assertEquals(listBookingDto, bookingService.getByIdOwnerBookingItems(1L, "CURRENT", 0, 4));
        Assertions.assertEquals(listBookingDto, bookingService.getByIdOwnerBookingItems(1L, "PAST", 0, 4));
        Assertions.assertEquals(listBookingDto, bookingService.getByIdOwnerBookingItems(1L, "FUTURE", 0, 4));
        Assertions.assertEquals(listBookingDto, bookingService.getByIdOwnerBookingItems(1L, "WAITING", 0, 4));
        Assertions.assertEquals(listBookingDto, bookingService.getByIdOwnerBookingItems(1L, "REJECTED", 0, 4));
    }

    @Test
    void pagedTest() {
        Pageable page = PageRequest.of(0, 4);

        Assertions.assertEquals(page, bookingService.paged(0, 4));
    }

    @Test
    void validationTest() {
        Item itemErr = Item.builder().id(1L).owner(owner).name("Item").description("Item items").available(false).request(null).build();
        BookingDto bookingDtoErrOne = BookingDto.builder().start(end).end(start).status(BookingStatus.WAITING).booker(user).item(item).build();
        BookingDto bookingDtoErrTwo = BookingDto.builder().start(start).end(end).status(BookingStatus.WAITING).booker(user).item(itemErr).build();

        final ValidationException exceptionOne = assertThrows(ValidationException.class, () -> bookingService.validation(bookingDtoErrOne, 1L));
        final ValidationException exceptionTwo = assertThrows(ValidationException.class, () -> bookingService.validation(bookingDtoErrTwo, 1L));

        Assertions.assertEquals(exceptionOne.getMessage(), "Указаны неправильные даты бронирования.");
        Assertions.assertEquals(exceptionTwo.getMessage(), "Вещь недоступна для аренды.");
    }

    @Test
    public void bookingAddErrTest() {
        BookingShort bookingShort = BookingShort.builder().start(start).end(end).itemId(1L).build();

        Mockito.when(userService.findById(anyLong()))
                .thenReturn(owner);

        Mockito.when(itemService.getById(anyLong()))
                .thenReturn(item);
        final NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.add(user.getId(), bookingShort));

        Assertions.assertEquals(exception.getMessage(), "Пользователь не владелец предмета бронирования.");
    }

    @Test
    public void bookingConfirmErrTest() {
        Optional<Booking> bookingOptional = Optional.of(Booking.builder().id(1L).start(start).end(end).item(item).booker(user).bookingStatus(BookingStatus.APPROVED).build());

        Mockito.when(bookingRepository.findById(1L))
                .thenReturn(bookingOptional);
        final NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.bookingConfirm(user.getId(), 1L, true));
        final ValidationException exceptionTwo = assertThrows(ValidationException.class, () -> bookingService.bookingConfirm(owner.getId(), 1L, true));

        Assertions.assertEquals(exception.getMessage(), "При подтверждение бронирования вещь с ID=" + booking.getItem().getId() + " не найдена!");
        Assertions.assertEquals(exceptionTwo.getMessage(), "Бронирование уже подтверждено");
    }

    @Test
    void getByIdBookingErrTest() {
        Optional<Booking> bookingOptional = Optional.of(Booking.builder().id(1L).start(start).end(end).item(item).booker(user).bookingStatus(BookingStatus.WAITING).build());

        Mockito.when(bookingRepository.findById(1L))
                .thenReturn(bookingOptional);
        Mockito.when(bookingRepository.getByIdBooking(1L, 1L))
                .thenReturn(booking);

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.getByIdBooking(3L, 1L));

        Assertions.assertEquals(exception.getMessage(), "Бронирование с ID= " + 1L + " у пользователя с ID= " + 3L + " не найдено!");
    }

    @Test
    void getByIdListErrBookingsTest() {
        List<Booking> listBooking = List.of(Booking.builder().start(start).end(end).item(item).booker(user).bookingStatus(BookingStatus.WAITING).build());

        Mockito.when(bookingRepository.getBookingByBookerIdAll(1L, Pageable.ofSize(4)))
                .thenReturn(listBooking);

        final NotStateException exception = assertThrows(NotStateException.class, () -> bookingService.getByIdListBookings(1L, "STATE", 0, 4));

        Assertions.assertEquals(exception.getMessage(), "Unknown state: UNSUPPORTED_STATUS");
    }

    @Test
    void getByIdOwnerBookingItemsErrTest() {
        List<Booking> listBooking = List.of(Booking.builder().start(start).end(end).item(item).booker(user).bookingStatus(BookingStatus.WAITING).build());

        Mockito.when(bookingRepository.getBookingByOwnerIdAll(1L, Pageable.ofSize(4)))
                .thenReturn(listBooking);

        final NotStateException exception = assertThrows(NotStateException.class, () -> bookingService.getByIdOwnerBookingItems(1L, "STATE", 0, 4));

        Assertions.assertEquals(exception.getMessage(), "Unknown state: UNSUPPORTED_STATUS");
    }

    @Test
    void pagedErrTest() {
        final NotStateException exception = assertThrows(NotStateException.class, () -> bookingService.paged(-1, 4));

        Assertions.assertEquals(exception.getMessage(), "Номер первого элемента неможет быть отрицательным.");
    }
}