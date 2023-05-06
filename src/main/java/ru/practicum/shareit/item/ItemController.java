package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getByUserId(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllItem(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getByItemId(@PathVariable Long itemId) {
        log.info("Выполняется запрос GET/items/{itemId} на получение вещи с id= {}", itemId);
        return itemService.getById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@NotBlank @RequestParam String text) {
        log.info("Выполняется запрос GET/items/search на поиск  {}", text);
        return itemService.search(text);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Выполняется запрос Post /items для добавление вещи {}, пользователя с id {}", itemDto, userId);
        return itemService.add(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemDto.setId(itemId);
        log.info("Выполняется запрос Post /items/{itemId} для обнавления вещи id ={}}", itemId);
        return itemService.update(userId, itemDto);
    }
}
