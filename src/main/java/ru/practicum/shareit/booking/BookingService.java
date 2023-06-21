package ru.practicum.shareit.booking;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotStateException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@NoArgsConstructor
@Transactional
@Slf4j
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    public BookingDto add(Long userId, BookingShort bookingShort) {
        //User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " при добавление бронирования не найден!"));
        User user = userService.findById(userId);
        Item item = itemService.getById(bookingShort.getItemId());
        // Item item = itemRepository.findById(bookingShort.getItemId()).orElseThrow(() -> new NotFoundException("Вещь с ID=" + bookingShort.getItemId() + " при добавление бронирования не найдена!"));
        if (!item.getOwner().equals(user)) {
            BookingDto bookingDto = BookingDto.builder()
                    .start(bookingShort.getStart())
                    .end(bookingShort.getEnd())
                    .booker(user)
                    .item(item)
                    .status(BookingStatus.WAITING)
                    .build();

            validation(bookingDto, userId);
            log.info("Добавлено бронирование {}", bookingDto);
            return BookingMapper.toBookingDto(bookingRepository.save(BookingMapper.toBooking(bookingDto, item, user)));
        } else {
            throw new NotFoundException("Пользователь не владелец предмета бронирования.");
        }
    }

    public BookingDto bookingConfirm(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование с ID= " + bookingId + " при подтверждение не найдено!"));
        if (booking.getItem().getOwner().getId().equals(userId)) {
            if (booking.getBookingStatus() == BookingStatus.APPROVED) {
                throw new ValidationException("Бронирование уже подтверждено");
            }
            if (approved) {
                booking.setBookingStatus(BookingStatus.APPROVED);
            } else {
                booking.setBookingStatus(BookingStatus.REJECTED);
            }
            bookingRepository.save(booking);
            log.info("Изменен статус бронирования {}", booking);
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException("При подтверждение бронирования вещь с ID=" + booking.getItem().getId() + " не найдена!");
        }
    }

    public BookingDto getByIdBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование с ID= " + bookingId + " не найдено!"));
        //validation(booking, userId);
        if (Objects.equals(booking.getBooker().getId(), userId) || Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            log.info("Запрошено бронирование {} пользователя {}", bookingId, userId);
            return BookingMapper.toBookingDto(bookingRepository.getByIdBooking(bookingId, userId));
        } else {
            throw new NotFoundException("Бронирование с ID= " + bookingId + " у пользователя с ID= " + userId + " не найдено!");
        }
    }

    public List<BookingDto> getByIdListBookings(Long userId, String state, Integer from, Integer size) {
        Pageable page = paged(from, size);
        //userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + "не найден!"));
        validationExistUser(userId);
        try {
            StateStatus stateStatus = StateStatus.valueOf(state);
            switch (stateStatus) {
                case ALL:
                    log.info("Все бронирования пользователя c id = {}, статус ALL", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getBookingByBookerIdAll(userId, page));
                case CURRENT:
                    log.info("Все бронирования пользователя c id = {}, статус CURRENT", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getBookingByUserIdAndBookingStatusCurrent(userId, LocalDateTime.now(), page));
                case PAST:
                    log.info("Все бронирования пользователя c id = {}, статус PAST", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getBookingByUserIdAndBookingStatusPast(userId, LocalDateTime.now(), page));
                case FUTURE:
                    log.info("Все бронирования пользователя c id = {}, статус FUTURE", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getBookingByUserIdAndBookingStatusFuture(userId, LocalDateTime.now(), page));
                case WAITING:
                    log.info("Все бронирования пользователя c id = {}, статус WAITING", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getBookingByUserIdAndBookingStatusWaiting(userId, BookingStatus.WAITING, page));
                case REJECTED:
                    log.info("Все бронирования пользователя c id = {}, статус REJECTED", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getBookingByUserIdAndBookingStatusRejected(userId, BookingStatus.REJECTED, page));
                default:
                    return List.of();
            }
        } catch (IllegalArgumentException e) {
            throw new NotStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    // @Transactional(readOnly = true)
    public List<BookingDto> getByIdOwnerBookingItems(Long userId, String state, Integer from, Integer size) {
        Pageable page = paged(from, size);
        // userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + "не найден!"));
        validationExistUser(userId);
        try {
            StateStatus stateStatus = StateStatus.valueOf(state);
            switch (stateStatus) {
                case ALL:
                    log.info("Все бронирования владельца вещей c id = {}, статус ALL", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getBookingByOwnerIdAll(userId, page));
                case CURRENT:
                    log.info("Все бронирования владельца вещей c id = {}, статус CURRENT", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getByItemOwnerIdAndBookingStatusCurrent(userId, LocalDateTime.now(), page));
                case PAST:
                    log.info("Все бронирования владельца вещей c id = {}, статус PAST", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getByItemOwnerIdAndBookingStatusPast(userId, LocalDateTime.now(), page));
                case FUTURE:
                    log.info("Все бронирования владельца вещей c id = {}, статус FUTURE", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getBookingByItemOwnerIdAndBookingStatusFuture(userId, LocalDateTime.now(), page));
                case WAITING:
                    log.info("Все бронирования владельца вещей c id = {}, статус WAITING", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getBookingByItemOwnerIdAndBookingStatusWaiting(userId, BookingStatus.WAITING, page));
                case REJECTED:
                    log.info("Все бронирования владельца вещей c id = {}, статус REJECTED", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getBookingByItemOwnerIdAndBookingStatusRejected(userId, BookingStatus.REJECTED, page));
                default:
                    return List.of();
            }
        } catch (IllegalArgumentException e) {
            throw new NotStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    void validation(BookingDto booking, Long ownerId) {
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().isEqual(booking.getStart())) {
            log.info("Указаны неправильные даты бронирования.");
            throw new ValidationException("Указаны неправильные даты бронирования.");
        }
        if (!booking.getItem().getAvailable()) {
            log.info("Вещь недоступна для аренды.");
            throw new ValidationException("Вещь недоступна для аренды.");
        }
       // if (booking.getStart() == null || booking.getEnd() == null
        //        || booking.getEnd().isBefore(booking.getStart())) {
        //    log.info("Неуказаны даты бронирования.");
        //    throw new ValidationException("Неуказаны даты бронирования.");
       // }
       // if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().isEqual(booking.getStart())) {
       //     log.info("Неправильные даты бронирования");
       //     throw new NotFoundException("Неправильные даты бронирования");
       // }
    }

    public Pageable paged(Integer from, Integer size) {
        Pageable page;
        if (from != null && size != null) {
            if (from < 0 || size < 0) {
                throw new NotStateException("Номер первого элемента неможет быть отрицательным.");
            }
            page = PageRequest.of(from > 0 ? from / size : 0, size);
        } else {
            page = PageRequest.of(0, 4);
        }
        return page;
    }

    User validationExistUser(Long userId) {
        return userService.findById(userId);
    }
}


