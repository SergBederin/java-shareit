package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-item-requests.
 */
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Выполняется запрос POST/requests с добавлением описания необходимой вещи: {} ", itemRequestDto);
        return itemRequestClient.addRequest(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long requestId) {
        log.info("Выполняется запрос GET /requests/{requestId}, получить данные о запросе c id = {}, вместе с данными об ответах на него ", requestId);
        return itemRequestClient.getRequest(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequests(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Выполняется запрос GET /requests/all?from={from}&size={size}, получить список запросов, созданных другими пользователями");
        return itemRequestClient.getRequests(userId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestToRequestor(@NotNull @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Выполняется запрос GET/requests, получить список запросов пользователя с id = {}, вместе с данными об ответах на них. ", userId);
        return itemRequestClient.getRequestToRequestor(userId);
    }
}