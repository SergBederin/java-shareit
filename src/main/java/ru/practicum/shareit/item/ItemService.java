package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Comment.CommentService;
import ru.practicum.shareit.Comment.dto.CommentDto;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingWithDate;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentService commentService;

    public ItemDto add(ItemDto itemDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Вещь не добавлена. Указанный пользователь с ID=" + userId + " не найден!"));
        Item item = ItemMapper.toItem(itemDto, user);
        validate(item);
        log.info("Добавлена вещь {}", item);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    public ItemDto update(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден!"));
        Item item = ItemMapper.toItem(itemDto, user);
        validateForUpdate(item, userId);
        Item itemUpd = itemRepository.findById(item.getId()).orElseThrow(() -> new NotFoundException("Вещь с ID=" + item.getId() + " не найдена!"));
        if (item.getName() != null) {
            itemUpd.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemUpd.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemUpd.setAvailable(item.getAvailable());
        }
        if (item.getRequestId() != null) {
            itemUpd.setRequestId(item.getRequestId());
        }
        return ItemMapper.toItemDto(itemRepository.save(itemUpd));
    }

    public ItemDtoWithBookingAndComments getByItemId(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID=" + itemId + " не найдена!"));
        BookingWithDate bookingLast = BookingMapper.mapToBookingWithoutDate(bookingRepository.findByItemIdLast(userId, itemId, LocalDateTime.now()));
        BookingWithDate bookingNext = BookingMapper.mapToBookingWithoutDate(bookingRepository.findByItemIdNext(userId, itemId, LocalDateTime.now()));
        List<CommentDto> comments = commentService.getCommentsByItemId(itemId);
        log.info("Запрошена вещь id={}, пользователя с id= {}", itemId, userId);
        return ItemMapper.mapToItemDtoWithBookingAndComments(item, bookingLast, bookingNext, comments);
    }

    public List<ItemDtoWithBookingAndComments> getAllItemByUser(Long userId) {
        HashMap<Long, BookingWithDate> bookingsLast = new HashMap<>();
        HashMap<Long, BookingWithDate> bookingsNext = new HashMap<>();
        HashMap<Long, List<CommentDto>> comments = new HashMap<>();
        List<Item> items = itemRepository.findByOwnerId(userId);
        for (Item i : items) {
            bookingsLast.put(i.getId(), BookingMapper.mapToBookingWithoutDate(bookingRepository.findByItemIdLast(userId, i.getId(), LocalDateTime.now())));
            bookingsNext.put(i.getId(), BookingMapper.mapToBookingWithoutDate(bookingRepository.findByItemIdNext(userId, i.getId(), LocalDateTime.now())));
            comments.put(i.getId(), commentService.getCommentsByItemId(i.getId()));
        }
        log.info("Запрошены вещи владельца с ID={}", userId);
        return ItemMapper.mapToItemDtoWithBookingAndComments(items, bookingsLast, bookingsNext, comments);
    }

    public List<ItemDto> search(String text) {
        if (!text.isEmpty()) {
            List<ItemDto> listSearch = new ArrayList<>();
            for (Item item : itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(text, text, true)) {
                listSearch.add(ItemMapper.toItemDto(item));
            }
            log.info("Запрошен поиск по строке {},найдено {}", text, listSearch.size());
            return listSearch;
        } else {
            return List.of();
        }
    }

    private void validate(Item item) {
        if (item.getOwner() == null) {
            log.info("Неуказан собственник вещи с {} ", item);
            throw new ValidationException("Неуказан собственник вещи");
        }
    }

    private void validateForUpdate(Item item, Long userId) {
        if (itemRepository.findById(item.getId()).isEmpty() || userRepository.findById(userId).isEmpty()) {
            log.info("Невозможно обновить. Запрошеная вещь не найдена id={}", item.getId());
            throw new NotFoundException("Невозможно обновить вещь.");
        }
        if (!itemRepository.findById(item.getId()).orElseThrow().getOwner().getId().equals(userId)) {
            log.info("Невозможно обновить. Собственники у вещи разные id={}", item.getId());
            throw new NotFoundException("Невозможно обновить вещь.");
        }
    }
}
