package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotRequestException;
import ru.practicum.shareit.exception.NotStateException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    public ItemRequestDto addRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = validationUser(userId);
        log.info("Пользователь найден {}", user);
        itemRequestDto.setRequestor(userId);
        if (itemRequestDto.getCreated() == null) {
            itemRequestDto.setCreated(LocalDateTime.now());
        }
        //itemRequestDto.setCreated(LocalDateTime.now());
        log.info("Добавлен новый запрос на вещь {}, пользователя {}", itemRequestDto, user);
        return ItemRequestMapper.mapToItemRequestDto(itemRequestRepository.save(ItemRequestMapper.mapToItemRequest(itemRequestDto, user)));
    }

    public List<ItemRequestDto> getRequest(Long userId) {
        validationUser(userId);
        List<ItemRequestDto> listItemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequestRepository.findItemRequestByRequestor_Id(userId));
        for (ItemRequestDto item : listItemRequestDto) {
            item.setItems(ItemMapper.toItemDto(itemRepository.findItemByRequest_Id(item.getId())));
        }
        log.info("Получен список запросов пользователя с id = {}, вместе с данными об ответах на них.", userId);
        return listItemRequestDto;
    }

    public List<ItemRequestDto> getRequestAll(Long userId, Integer from, Integer size) {
        if (from == null || size == null) {
            return List.of();
        } else if (from >= 0) {
            Pageable page = PageRequest.of(from / size, size);
            validationUser(userId);
            List<ItemRequestDto> listItemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequestRepository.findItemRequestByIdNotOrderByCreatedDesc(userId, page));
            for (ItemRequestDto item : listItemRequestDto) {
                item.setItems(ItemMapper.toItemDto(itemRepository.findItemByRequest_Id(item.getId())));
            }
            log.info("Получен список запросов, созданный пользователем c id = {}.", userId);
            return listItemRequestDto;
        } else {
            throw new NotStateException("Первый элемент списка неможет быть отрицательным или равным 0.");
        }
    }

    public ItemRequestDto getRequestId(Long userId, Long requestId) {
        validationUser(userId);
        ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotRequestException("Запрос c id =" + requestId + " не найден.")));
        itemRequestDto.setItems(ItemMapper.toItemDto(itemRepository.findItemByRequest_Id(requestId)));
        log.info("Получены данные об одном запросе c id = {}, вместе с данными об ответах на него.", requestId);
        return itemRequestDto;
    }

    User validationUser(Long userId) {
        return UserMapper.toUser(userService.getById(userId));
    }
}
