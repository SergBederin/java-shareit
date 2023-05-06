package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserStorageInMemory;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final UserStorageInMemory userStorage;
    private final UserService userService;
    private final ItemStorageInMemory itemStorage;
    private final ItemMapper itemMapper;

    public ItemDto add(ItemDto itemDto, Long userId) {
        Item item = itemMapper.toItem(itemDto, userId);
        validate(item);
        userService.getById(userId);
        return itemMapper.toItemDto(itemStorage.createItem(item));
    }

    public ItemDto update(Long userId, ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto, userId);
        validateForUpdate(item, userId);
        if (!userStorage.getStorageUser().containsKey(item.getOwnerId())) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с ID=" + userId + " не найден!");
        }
        return itemMapper.toItemDto(itemStorage.updateItem(userId, item));
    }

    public ItemDto getById(Long id) {
        if (!itemStorage.getStorageItems().containsKey(id) || id.equals(null)) {
            throw new NotFoundException(HttpStatus.BAD_REQUEST, "Пользователь с ID=" + id + " не найден!");
        }
        return itemMapper.toItemDto(itemStorage.getItemById(id));
    }

    public List<ItemDto> getAllItem(long userId) {
        List<ItemDto> listDto = new ArrayList<>();
        for (Item item : itemStorage.getAllItemUserId(userId)) {
            listDto.add(itemMapper.toItemDto(item));
        }
        return listDto;
    }

    public List<ItemDto> search(String text) {
        if (!text.isEmpty()) {
            List<ItemDto> listDto = new ArrayList<>();
            for (Item item : itemStorage.searchItem(text)) {
                listDto.add(itemMapper.toItemDto(item));
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
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Неуказан собственник вещи");
        }
        if (item.getAvailable() == null || item.getName().equals("") || item.getDescription() == null || item.getName() == null) {
            log.info("Неуказано название вещи с  id={} или нет описания.", item.getId());
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Неуказано название вещи или нет описания.");
        }
    }

    private void validateForUpdate(Item item, Long userId) {
        if (itemStorage.getItemById(item.getId()) == null) {
            log.info("Невозможно обновить. Запрошеная вещь не найдена /id={}/", item.getId());
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Невозможно обновить вещь.");
        }
        if (!(itemStorage.getItemById(item.getId()).getOwnerId().equals(userId))) {
            log.info("Невозможно обновить. Собственники у вещи разные /id={}/", item.getId());
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Невозможно обновить вещь.");
        }
    }
}
