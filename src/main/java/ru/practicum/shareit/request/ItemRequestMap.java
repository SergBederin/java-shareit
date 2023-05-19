package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class ItemRequestMap {
    public ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto, User user) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .requestor(user)
                .build();
    }

    public ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor().getId())
                .build();
    }

    public List<ItemRequestDto> mapToUserDto(List<ItemRequest> itemRequestList) {
        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestList) {
            itemRequestDtoList.add(mapToItemRequestDto(itemRequest));
        }
        return itemRequestDtoList;
    }
}