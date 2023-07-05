package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-controllers.
 */
@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @Valid @RequestBody ItemDto itemDtogateway) {
        log.info("Выполняется запрос Post /items для добавление вещи {}, пользователя с id {}", itemDtogateway, userId);
        return itemClient.addItem(userId, itemDtogateway);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId,
                                             @RequestBody @Valid CommentDto commentDtoGateway) {
        log.info("Выполняется запрос /{itemId}/comment для добавления комментария к вещи id ={}", itemId);
        return itemClient.addComment(userId, itemId, commentDtoGateway);
    }

    @PatchMapping("/{itemId}")
    @Validated(Marker.OnUpdate.class)
    public ResponseEntity<Object> updateItem(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId,
                                             @RequestBody ItemDto itemDtogateway) {
        log.info("Выполняется запрос Patch/items/{itemId} для обнавления вещи id ={}", itemId);
        return itemClient.upItem(userId, itemId, itemDtogateway);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Выполняется запрос GET/items/search на поиск  {}", text);
        return itemClient.searchItem(text, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long itemId) {
        log.info("Выполняется запрос GET/items/{itemId} на получение вещи с id= {}, пользователя с id= {} ", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                           @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Выполняется запрос GET/items на получение вещей пользователя с id= {}", userId);
        return itemClient.getItems(userId, from, size);
    }
}