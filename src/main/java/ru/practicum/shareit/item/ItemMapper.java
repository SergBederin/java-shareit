package ru.practicum.shareit.item;


import ru.practicum.shareit.Comment.dto.CommentDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithDate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item toItem(ItemDto itemDto, User user) {
        return Item.builder()
                .id(itemDto.getId())
                .owner(user)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static ItemDtoWithBookingAndComments mapToItemDtoWithBookingAndComments(Item item, BookingDtoWithDate bookingLast, BookingDtoWithDate bookingNext, List<CommentDto> comments) {
        return ItemDtoWithBookingAndComments.builder()
                .id(item.getId())
                .owner(item.getOwner().getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(bookingLast)
                .nextBooking(bookingNext)
                .comments(comments)
                .build();
    }

    public static List<ItemDtoWithBookingAndComments> mapToItemDtoWithBookingAndComments(List<Item> listItem, HashMap<Long, BookingDtoWithDate> bookingsLast, HashMap<Long, BookingDtoWithDate> bookingsNext, HashMap<Long, List<CommentDto>> comments) {
        List<ItemDtoWithBookingAndComments> listItemDtoWithBooking = new ArrayList<>();
        for (Item item : listItem) {
            BookingDtoWithDate bookingLast = bookingsLast.get(item.getId());
            BookingDtoWithDate bookingNext = bookingsNext.get(item.getId());
            List<CommentDto> commentList = comments.get(item.getId());
            listItemDtoWithBooking.add(mapToItemDtoWithBookingAndComments(item, bookingLast, bookingNext, commentList));
        }
        return listItemDtoWithBooking.stream()
                .sorted(Comparator.comparing(ItemDtoWithBookingAndComments::getId))
                .collect(Collectors.toList());
    }
}
