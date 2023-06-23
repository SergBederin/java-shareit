package ru.practicum.shareit.booking;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingJsonTest {
    @Autowired
    private JacksonTester<BookingShortDto> jsonBookingShort;

    @Test
    void testBookingShort() throws IOException {
        LocalDateTime start = LocalDateTime.of(2025,5,25,14,15,13);
        LocalDateTime end = LocalDateTime.of(2025,5,25,15,15,13);
        BookingShortDto bookingShortDto = BookingShortDto.builder().start(start).end(end).itemId(1L).build();

        JsonContent<BookingShortDto> result = jsonBookingShort.write(bookingShortDto);

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.toString());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }
}
