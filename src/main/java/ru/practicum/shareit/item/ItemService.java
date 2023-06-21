package ru.practicum.shareit.item;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoWithDate;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotStateException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@Transactional
@NoArgsConstructor
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserService userService;

    public ItemDto add(ItemDto itemDto, Long userId) {
        //User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Вещь не добавлена. Указанный пользователь с ID=" + userId + " не найден!"));
        User user = userService.findById(userId);
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestService.getRequestId(userId, itemDto.getRequestId()), user);
        }
        Item item = ItemMapper.toItem(itemDto, user, itemRequest);
        //validate(item);
        log.info("Добавлена вещь {}", item);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        validateExistUser(userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден!"));
        ItemDto itemDtoOld = ItemMapper.mapToItemWithBookingAndComments(getByItemId(userId, itemId));
        if (itemDto.getName() != null) {
            itemDtoOld.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemDtoOld.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemDtoOld.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getRequestId() != null) {
            itemDtoOld.setRequestId(itemDto.getRequestId());
        }
        log.info("Обавлена вещь {}", itemDto);
        return add(itemDtoOld, userId);
    }

    public ItemDtoWithBookingAndComments getByItemId(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с ID=" + itemId + " не найдена!"));
        BookingDtoWithDate bookingLast = BookingMapper.mapToBookingWithoutDate(bookingRepository.findByItemIdLast(userId, itemId, LocalDateTime.now()));
        BookingDtoWithDate bookingNext = BookingMapper.mapToBookingWithoutDate(bookingRepository.findByItemIdNext(userId, itemId, LocalDateTime.now()));
        List<CommentDto> comments = commentService.getCommentsByItemId(itemId);
        log.info("Запрошена вещь id={}, пользователя с id= {}", itemId, userId);
        return ItemMapper.mapToItemDtoWithBookingAndComments(item, bookingLast, bookingNext, comments);
    }

    public List<ItemDtoWithBookingAndComments> getAllItemByUser(Long userId, Integer from, Integer size) {
        Pageable page = paged(from, size);
        HashMap<Long, BookingDtoWithDate> bookingsLast = new HashMap<>();
        HashMap<Long, BookingDtoWithDate> bookingsNext = new HashMap<>();
        HashMap<Long, List<CommentDto>> comments = new HashMap<>();
        List<Item> items = itemRepository.findByOwnerId(userId, page);
        for (Item i : items) {
            bookingsLast.put(i.getId(), BookingMapper.mapToBookingWithoutDate(bookingRepository.findByItemIdLast(userId, i.getId(), LocalDateTime.now())));
            bookingsNext.put(i.getId(), BookingMapper.mapToBookingWithoutDate(bookingRepository.findByItemIdNext(userId, i.getId(), LocalDateTime.now())));
            comments.put(i.getId(), commentService.getCommentsByItemId(i.getId()));
        }
        return ItemMapper.mapToItemDtoWithBookingAndComments(items, bookingsLast, bookingsNext, comments);
    }

    @Transactional(readOnly = true)
    public Item getById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID=" + itemId + " не найдена!"));
    }

    public List<ItemDto> search(String text, Integer from, Integer size) {
        if (!text.isEmpty()) {
            Pageable page = paged(from, size);
            return ItemMapper.toItemDto(itemRepository.search(text, page));
        } else {
            return List.of();
        }
    }

    /* private void validate(Item item) {
         if (item.getOwner() == null) {
             log.info("Неуказан собственник вещи с {} ", item);
             throw new ValidationException("Неуказан собственник вещи");
         }
     }*/
    void validateExistUser(Long userId) {
        userService.findById(userId);
    }

  /*  private void validateForUpdate(Item item, Long userId) {
        if (itemRepository.findById(item.getId()).isEmpty() || userRepository.findById(userId).isEmpty()) {
            log.info("Невозможно обновить. Запрошеная вещь не найдена id={}", item.getId());
            throw new NotFoundException("Невозможно обновить вещь.");
        }
        if (!itemRepository.findById(item.getId()).orElseThrow().getOwner().getId().equals(userId)) {
            log.info("Невозможно обновить. Собственники у вещи разные id={}", item.getId());
            throw new NotFoundException("Невозможно обновить вещь.");
        }
    }*/

    public Pageable paged(Integer from, Integer size) {
        if (from != null && size != null) {
            if (from < 0) {
                throw new NotStateException("Номер первого элемента неможет быть отрицательным.");
            }
            return PageRequest.of(from > 0 ? from / size : 0, size);
        } else {
            return PageRequest.of(0, 4);
        }
    }

    //   @Transactional(readOnly = true)
    // public Item findById(Long itemId) {
    //    return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с ID=" + itemId + " не найдена!"));
    // }

}
