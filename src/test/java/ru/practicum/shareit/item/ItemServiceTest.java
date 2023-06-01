package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotStateException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @InjectMocks
    private ItemService itemService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentService commentService;

    static User user;
    static UserDto userDto;
    static User owner;
    static Item item;
    static Booking booking;
    static List<CommentDto> listCommentDto;
    static LocalDateTime start;
    static LocalDateTime end;

    @BeforeAll
    static void init() {
        user = User.builder().id(1L).name("User").email("user@user.ru").build();
        userDto = UserDto.builder().id(1L).name("User").email("user@user.ru").build();
        owner = User.builder().id(2L).name("Owner").email("owner@user.ru").build();
        item = Item.builder().owner(owner).name("Item").description("Item items").available(true).request(null).build();
        start = LocalDateTime.now();
        end = LocalDateTime.now().plusMinutes(1);
        booking = Booking.builder().start(start).end(end).item(item).booker(user).bookingStatus(BookingStatus.WAITING).build();
        listCommentDto = List.of(CommentDto.builder().text("Text test").authorName(user.getName()).created(end).build());
    }

    @Test
    public void addTest() {

        ItemDto itemDto = ItemDto.builder().name("Item").description("Item items").available(true).requestId(null).build();
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);
        ItemDto itemResult = itemService.add(itemDto, userDto.getId());
        Assertions.assertEquals(itemResult, itemDto);
    }

    @Test
    void updateTest() {
        Item itemUpdate = Item.builder().owner(owner).name("Update").description("Update").available(false).request(null).build();
        ItemDto itemDtoUpdate = ItemDto.builder().name("Update").description("Update").available(false).requestId(null).build();
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(itemUpdate);
        assertEquals(itemDtoUpdate, itemService.update(1L, 1L, itemDtoUpdate));
    }

    @Test
    void getByItemIdTest() {
        Mockito.when(itemRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(item));
        ItemDto itemResult = ItemMapper.mapToItemWithBookingAndComments(itemService.getByItemId(1L, 1L));
        Assertions.assertEquals(itemResult, ItemMapper.toItemDto(item));
    }

    @Test
    void pagedTest() {
        Pageable page = PageRequest.of(0, 4);
        Assertions.assertEquals(page, itemService.paged(0, 4));
    }

    //Reaction to erroneous data
    @Test
    void searchErrTest() {
        Assertions.assertEquals(List.of(), itemService.search("test", 0, 4));
    }

    @Test
    void pagedErrTest() {
        final NotStateException exception = assertThrows(NotStateException.class, () -> itemService.paged(-1, 4));

        Assertions.assertEquals(exception.getMessage(), "Номер первого элемента неможет быть отрицательным.");
    }
}