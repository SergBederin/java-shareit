package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotStateException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingDto add(Long userId, BookingDto bookingDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " при добавление бронирования не найден!"));
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Вещь с ID=" + bookingDto.getItemId() + " при добавление бронирования не найдена!"));
        if (!item.getOwner().equals(user)) {
            Booking booking = BookingMapper.toBooking(bookingDto, item, user);
            validation(booking, userId);
            booking.setBookingStatus(BookingStatus.WAITING);
            bookingRepository.save(booking);
            log.info("Добавлено бронирование {}", booking);
            return BookingMapper.toBookingDto(booking);
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

    public List<BookingDto> getByIdListBookings(Long userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + "не найден!"));
        try {
            StateStatus stateStatus = StateStatus.valueOf(state);
            switch (stateStatus) {
                case ALL:
                    log.info("Все бронирования пользователя c id = {}, статус ALL", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getBookingByBookerIdAll(userId));
                //  return BookingMapper.mapToBookingDto(bookingRepository.findAllBookingByBookerId(userId));

                case CURRENT:
                    log.info("Все бронирования пользователя c id = {}, статус CURRENT", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getBookingByUserIdAndBookingStatusCurrent(userId, LocalDateTime.now()));
                case PAST:
                    log.info("Все бронирования пользователя c id = {}, статус PAST", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getBookingByUserIdAndBookingStatusPast(userId, LocalDateTime.now()));
                case FUTURE:
                    log.info("Все бронирования пользователя c id = {}, статус FUTURE", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getBookingByUserIdAndBookingStatusFuture(userId, LocalDateTime.now()));
                case WAITING:
                    log.info("Все бронирования пользователя c id = {}, статус WAITING", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getBookingByUserIdAndBookingStatusWaiting(userId, BookingStatus.WAITING));
                case REJECTED:
                    log.info("Все бронирования пользователя c id = {}, статус REJECTED", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getBookingByUserIdAndBookingStatusRejected(userId, BookingStatus.REJECTED));
                default:
                    return List.of();
            }
        } catch (IllegalArgumentException e) {
            // throw new NotStateException("Тип бронирования не указан {}." + state);
            throw new NotStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    public List<BookingDto> getByIdOwnerBookingItems(Long userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + "не найден!"));
        try {
            StateStatus stateStatus = StateStatus.valueOf(state);
            switch (stateStatus) {
                case ALL:
                    log.info("Все бронирования владельца вещей c id = {}, статус ALL", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getBookingByOwnerIdAll(userId));
                case CURRENT:
                    log.info("Все бронирования владельца вещей c id = {}, статус CURRENT", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getByItemOwnerIdAndBookingStatusCurrent(userId, LocalDateTime.now()));
                case PAST:
                    log.info("Все бронирования владельца вещей c id = {}, статус PAST", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getByItemOwnerIdAndBookingStatusPast(userId, LocalDateTime.now()));
                case FUTURE:
                    log.info("Все бронирования владельца вещей c id = {}, статус FUTURE", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getBookingByItemOwnerIdAndBookingStatusFuture(userId, LocalDateTime.now()));
                case WAITING:
                    log.info("Все бронирования владельца вещей c id = {}, статус WAITING", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getBookingByItemOwnerIdAndBookingStatusWaiting(userId, BookingStatus.WAITING));
                case REJECTED:
                    log.info("Все бронирования владельца вещей c id = {}, статус REJECTED", userId);
                    return BookingMapper.mapToBookingDto(bookingRepository.getBookingByItemOwnerIdAndBookingStatusRejected(userId, BookingStatus.REJECTED));
                default:
                    return List.of();
            }
        } catch (IllegalArgumentException e) {
            throw new NotStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    void validation(Booking booking, Long ownerId) {
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().isEqual(booking.getStart())) {
            log.info("Указаны неправильные даты бронирования.");
            throw new ValidationException("Указаны неправильные даты бронирования.");
        }
        if (!booking.getItem().getAvailable()) {
            log.info("Вещь недоступна для аренды.");
            throw new ValidationException("Вещь недоступна для аренды.");
        }
        if (booking.getItem() == null
                || booking.getBooker() == null) {
            log.info("Не указан пользователь или вещь.");
            throw new NotFoundException("Не указан пользователь или вещь.");
        }
        if (booking.getStart() == null || booking.getEnd() == null
                || booking.getEnd().isBefore(booking.getStart())) {
            log.info("Неуказаны даты бронирования.");
            throw new ValidationException("Неуказаны даты бронирования.");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            log.info("Неправильные даты бронирования");
            throw new NotFoundException("Неправильные даты бронирования");
        }
    }
}


