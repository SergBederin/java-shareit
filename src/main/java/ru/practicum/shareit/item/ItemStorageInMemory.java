package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ItemStorageInMemory implements ItemStorage {

    private final Map<Long, Item> storageItems = new HashMap<>();
    private final UserStorage userStorage;
    private Long idItem = 0L;

    private Long getNewId() {
        return ++idItem;
    }

    @Override
    public Item createItem(Item item) {
        validate(item);
        item.setId(getNewId());
        log.info("Выполнен Post /items В репозитории выход");
        storageItems.put(item.getId(), item);
        log.info("Добавлена вещь /{}/", item.toString());
        return item;
    }

    @Override
    public Item updateItem(Long userId, Item item) {

        validateForUpdate(item, userId);
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
        log.info("обновлена вещь /{}/", storageItems.get(item.getId()).toString());
        return storageItems.get(item.getId());
    }

    @Override
    public Item getItemById(Long itemId) {
        if (!storageItems.containsKey(itemId)) {
            throw new NotFoundException("Пользователь с ID=" + itemId + " не найден!");
        }
        log.info("Запрошена вещь /id={}/", itemId);
        return storageItems.get(itemId);
    }

    @Override
    public List<Item> getAllItemUserId(Long userId) {
        List<Item> itemList = new ArrayList<>();
        for (Item item : storageItems.values()) {
            if (item.getOwnerId() == (long) userId) {
                itemList.add(item);
            }
        }
        log.info("Запрошены вещи владельца /id={}/", userId);
        return itemList;
    }

    @Override
    public List<Item> searchItem(String text) {
        List<Item> itemList = new ArrayList<>();
        if (text == null || text.equals("")) {
            return itemList;
        }
        String[] split;
        boolean mismatch;
        for (Item item : storageItems.values()) {
            if (!item.getAvailable()) {
                continue;
            }
            mismatch = true;
            split = item.getName().split(" ");
            for (int i = 0; i < split.length; i++) {
                if (split[i].length() >= text.length()
                        && split[i].substring(0, text.length()).toLowerCase().equals(text.toLowerCase())) {
                    itemList.add(item);
                    mismatch = false;
                    break;
                }
            }
            if (!mismatch) {
                continue;
            }

            split = item.getDescription().split(" ");
            for (int i = 0; i < split.length; i++) {
                if (split[i].length() >= text.length()
                        && split[i].substring(0, text.length()).toLowerCase().equals(text.toLowerCase())) {
                    itemList.add(item);
                    break;
                }
            }
        }
        log.info("Запрошен поиск вещи по строке /text={}/, найдено {}", text, itemList.size());
        return itemList;
    }

    private void validate(Item item) {
        userStorage.getUserById(item.getOwnerId());
        if (item.getOwnerId() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (item.getAvailable() == null || item.getName().equals("") || item.getDescription() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private void validateForUpdate(Item item, Long userId) {
        if (storageItems.get(item.getId()) == null) {
            log.info("Невозможно обновить. Запрошеная вещь не найдена /id={}/", item.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (storageItems.get(item.getId()).getOwnerId() != userId) {
            log.info("Невозможно обновить. Собственники у вещи разные /id={}/", item.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
