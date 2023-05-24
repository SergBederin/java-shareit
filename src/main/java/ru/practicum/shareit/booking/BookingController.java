package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    BookingDto create(@Valid @RequestBody BookingDto bookingDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Выполняется запрос Post/bookings для добавление бронирования {}, пользователя с id {}", bookingDto, userId);
        return bookingService.add(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    BookingDto bookingConfirm(@RequestHeader("X-Sharer-User-Id") Long userId, @NotNull @PathVariable Long bookingId, @RequestParam boolean approved) {
        log.info("Выполняется запрос Patch/bookings/{bookingId} для подверждения бронирования с id ={} пользователя с id ={}}", bookingId, userId);
        return bookingService.bookingConfirm(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    BookingDto getByIdBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        log.info("Выполняется запрос GET/bookings/{bookingId} на получение бронировния пользователя с id= {}, и брони с id= {}", userId, bookingId);
        return bookingService.getByIdBooking(userId, bookingId);
    }

    @GetMapping
    List<BookingDto> getByIdListBookings(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(required = false, defaultValue = "ALL") String state, @RequestParam(required = false) Integer from, @RequestParam(required = false) Integer size) {
        log.info("Выполняется запрос GET/bookings/ на получение всех бронированйи пользователя с id= {}, и статусом {}", userId, state);
        return bookingService.getByIdListBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    List<BookingDto> getByIdOwnerBookingItems(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(required = false, defaultValue = "ALL") String state, @RequestParam(required = false) Integer from, @RequestParam(required = false) Integer size) {
        log.info("Выполняется запрос GET/bookings/owner на получение бронировния пользователя с id= {}, и статусом {}", userId, state);
        return bookingService.getByIdOwnerBookingItems(userId, state, from, size);
    }
}

