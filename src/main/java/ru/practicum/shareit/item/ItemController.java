package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
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
    private final ItemMapper itemMapper;

    @GetMapping
    public List<ItemDto> getByUserId(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemDto> listDto = new ArrayList<>();
        for (Item item : itemService.getAllItem(userId)) {
            listDto.add(itemMapper.toItemDto(item));
        }
        return listDto;
    }

    @GetMapping("/{itemId}")
    public ItemDto getByItemId(@PathVariable Long itemId) {
        return itemMapper.toItemDto(itemService.getById(itemId));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@NotBlank @RequestParam String text) {
        List<ItemDto> listDto = new ArrayList<>();
        for (Item item : itemService.search(text)) {
            listDto.add(itemMapper.toItemDto(item));
        }
        return listDto;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto, userId);
        log.info("Выполнен Post /items");
        return itemMapper.toItemDto(itemService.add(item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        Item item = itemMapper.toItem(itemDto, userId);
        item.setId(itemId);
        return itemMapper.toItemDto(itemService.update(userId, item, itemId));
    }
}
