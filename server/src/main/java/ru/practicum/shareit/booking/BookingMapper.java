package ru.practicum.shareit.booking;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithDate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto, Item item, User user) {
        return Booking.builder()
                .id(bookingDto.getId())
                .bookingStatus(bookingDto.getStatus())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(user)
                .build();
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getBookingStatus())
                .build();
    }

    public static List<BookingDto> mapToBookingDto(List<Booking> bookingList) {
        List<BookingDto> listBookingDto = new ArrayList<>();
        for (Booking booking : bookingList) {
            listBookingDto.add(toBookingDto(booking));
        }
        return listBookingDto;
    }

    public static BookingDtoWithDate mapToBookingWithoutDate(Booking booking) {
        if (booking != null) {
            return BookingDtoWithDate.builder()
                    .id(booking.getId())
                    .start(booking.getStart())
                    .end(booking.getEnd())
                    .status(booking.getBookingStatus())
                    .bookerId(booking.getBooker().getId())
                    .build();
        } else {
            return null;
        }
    }
}
