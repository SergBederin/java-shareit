package ru.practicum.shareit.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
@Getter
public class ItemStorageInMemory implements ItemStorage {

    private final Map<Long, Item> storageItems = new HashMap<>();
    private Long idItem = 0L;

    private Long getNewId() {
        return ++idItem;
    }

    @Override
    public Item createItem(Item item) {
        item.setId(getNewId());
        storageItems.put(item.getId(), item);
        log.info("Добавлена вещь /{}/", item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        Item itemUpd = storageItems.get(item.getId());
        if (item.getName() != null) {
            itemUpd.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemUpd.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemUpd.setAvailable(item.getAvailable());
        }
        storageItems.put(item.getId(), itemUpd);
        log.info("Обновлена вещь /{}/", storageItems.get(item.getId()).toString());
        return storageItems.get(item.getId());
    }

    @Override
    public Item getItemById(Long itemId) {
        log.info("Запрошена вещь /id={}/", itemId);
        return storageItems.get(itemId);
    }

    @Override
    public List<Item> getAllItemUserId(Long userId) {
        List<Item> itemList = storageItems.values()
                .stream()
                .filter(item -> (item.getOwnerId() == (long) userId))
                .collect(Collectors.toList());
        log.info("Запрошены вещи владельца /id={}/", userId);
        return itemList;
    }

    @Override
    public List<Item> searchItem(String text) {
        List<Item> searchItem = storageItems.values()
                .stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && item.getAvailable().equals(true))
                .collect(Collectors.toList());
        return searchItem;
    }
}
