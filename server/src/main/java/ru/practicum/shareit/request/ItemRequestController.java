package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    ItemRequestDto addRequest(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Выполняется запрос POST/requests с добавлением описания необходимой вещи: {} ", itemRequestDto);
        return itemRequestService.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    List<ItemRequestDto> getRequest(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Выполняется запрос GET/requests, получить список запросов пользователя с id = {}, вместе с данными об ответах на них. ", userId);
        return itemRequestService.getRequest(userId);
    }

    @GetMapping("/all")
    List<ItemRequestDto> getRequestAll(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(required = false) Integer from, @RequestParam(required = false) Integer size) {
        log.info("Выполняется запрос GET /requests/all?from={from}&size={size}, получить список запросов, созданных другими пользователями");
        return itemRequestService.getRequestAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    ItemRequestDto getRequestId(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        log.info("Выполняется запрос GET /requests/{requestId}, получить данные о запросе c id = {}, вместе с данными об ответах на него ", requestId);
        return itemRequestService.getRequestId(userId, requestId);
    }
}

