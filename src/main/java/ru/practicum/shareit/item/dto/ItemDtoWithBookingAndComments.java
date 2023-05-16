package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.Comment.dto.CommentDto;
import ru.practicum.shareit.booking.dto.BookingWithDate;

import java.util.List;

@Data
@Builder
public class ItemDtoWithBookingAndComments {
    private Long id;
    private Long owner;
    private String name;
    private String description;
    private Boolean available;
    private Long request;
    private BookingWithDate lastBooking;
    private BookingWithDate nextBooking;
    private List<CommentDto> comments;
}