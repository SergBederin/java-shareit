package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Comment.CommentService;
import ru.practicum.shareit.Comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;

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
    private final CommentService commentService;

    @GetMapping
    public List<ItemDtoWithBookingAndComments> getByUserId(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Выполняется запрос GET/items на получение вещей пользователя с id= {}", userId);
        return itemService.getAllItemByUser(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookingAndComments getByItemId(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Выполняется запрос GET/items/{itemId} на получение вещи с id= {}, пользователя с id= {} ", itemId, userId);
        return itemService.getByItemId(userId, itemId);
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
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemDto.setId(itemId);
        log.info("Выполняется запрос Patch/items/{itemId} для обнавления вещи id ={}", itemId);
        return itemService.update(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId, @Valid @RequestBody CommentDto commentDto) {
        log.info("Выполняется запрос /{itemId}/comment для добавления комментария к вещи id ={}", itemId);
        return commentService.addComment(userId, itemId, commentDto);
    }
}
