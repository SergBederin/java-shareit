package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @InjectMocks
    private BookingService bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private ItemRepository itemRepository;

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

        Mockito.when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));
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

        Mockito.when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
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
        Mockito.when(userRepository.findById(any()))
                .thenReturn(Optional.of(owner));
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
}