package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item createItem(Item item);

    Item updateItem(Long userId, Item item);

    Item getItemById(Long itemId);

    List<Item> getAllItemUserId(Long userId);

    List<Item> searchItem(String text);
}
