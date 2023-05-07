package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorageInMemory;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final UserStorageInMemory userStorage;
    private final ItemStorageInMemory itemStorage;

    public ItemDto add(ItemDto itemDto, Long userId) {
        Item item = ItemMapper.toItem(itemDto, userId);
        validate(item);
        if (!userStorage.getStorageUser().containsKey(userId)) {
            throw new NotFoundException("Вещь не добавлена. Пользователь с ID=" + userId + " не найден!");
        }
        return ItemMapper.toItemDto(itemStorage.createItem(item));
    }

    public ItemDto update(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto, userId);
        validateForUpdate(item, userId);
        if (!userStorage.getStorageUser().containsKey(item.getOwnerId())) {
            throw new NotFoundException("Пользователь с ID=" + userId + " не найден!");
        }
        return ItemMapper.toItemDto(itemStorage.updateItem(item));
    }

    public ItemDto getById(Long id) {
        if (!itemStorage.getStorageItems().containsKey(id) || id.equals(null)) {
            throw new NotFoundException("Пользователь с ID=" + id + " не найден!");
        }
        return ItemMapper.toItemDto(itemStorage.getItemById(id));
    }

    public List<ItemDto> getAllItem(long userId) {
        List<ItemDto> listDto = new ArrayList<>();
        for (Item item : itemStorage.getAllItemUserId(userId)) {
            listDto.add(ItemMapper.toItemDto(item));
        }
        return listDto;
    }

    public List<ItemDto> search(String text) {
        if (!text.isEmpty()) {
            List<ItemDto> listDto = new ArrayList<>();
            for (Item item : itemStorage.searchItem(text)) {
                listDto.add(ItemMapper.toItemDto(item));
            }
            return listDto;
        } else {
            return List.of();
        }
    }

    private void validate(Item item) {
        userStorage.getUserById(item.getOwnerId());
        if (item.getOwnerId() == null) {
            log.info("Неуказан собственник вещи с {} ", item);
            throw new NotFoundException("Неуказан собственник вещи");
        }
        if (item.getAvailable() == null || item.getName().equals("") || item.getDescription() == null || item.getName() == null) {
            log.info("Неуказано название вещи с  id={} или нет описания.", item.getId());
            throw new ValidationException("Неуказано название вещи или нет описания.");
        }
    }

    private void validateForUpdate(Item item, Long userId) {
        if (itemStorage.getItemById(item.getId()) == null) {
            log.info("Невозможно обновить. Запрошеная вещь не найдена /id={}/", item.getId());
            throw new NotFoundException("Невозможно обновить вещь.");
        }
        if (!(itemStorage.getItemById(item.getId()).getOwnerId().equals(userId))) {
            log.info("Невозможно обновить. Собственники у вещи разные /id={}/", item.getId());
            throw new NotFoundException("Невозможно обновить вещь.");
        }
    }
}
