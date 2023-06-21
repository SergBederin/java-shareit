package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;


class ItemRequestServiceTest {
    static ItemRequestService itemRequestService = new ItemRequestService();
    static ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    static ItemRequestRepository itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
    static UserService userService = Mockito.mock(UserService.class);
    static User user;
    static User user1;
    static UserDto userDto;
    static ItemRequestDto requestDto;
    static ItemRequest request;

    @BeforeAll
    static void init() {
        ReflectionTestUtils.setField(itemRequestService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemRequestService, "itemRequestRepository", itemRequestRepository);
        ReflectionTestUtils.setField(itemRequestService, "userService", userService);
        user = User.builder().id(1L).name("User").email("user@user.ru").build();
        user1 = User.builder().id(1L).name("User1").email("user1@user.ru").build();
        userDto = UserDto.builder().id(1L).name("User").email("user@user.ru").build();
        requestDto = ItemRequestDto.builder().id(1L).description("test").requestor(1L).created(LocalDateTime.now().withNano(0)).build();
        request = ItemRequest.builder().id(1L).description("test").requestor(user).created(LocalDateTime.now().withNano(0)).build();
    }

    @Test
    void addRequestTest() {
        LocalDateTime start = LocalDateTime.now();
        ItemRequest itemRequest = ItemRequest.builder().id(1L).description("Test").created(start).requestor(user).build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().id(1L).description("Test").created(start).requestor(user.getId()).items(null).build();

        Mockito.when(userService.getById(anyLong()))
                .thenReturn(userDto);
        Mockito.when(itemRequestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(itemRequest);

        assertEquals(itemRequestDto, itemRequestService.addRequest(1L, itemRequestDto));
    }

    @Test
    void getRequestTest() {
        LocalDateTime start = LocalDateTime.now();
        User user = User.builder().id(1L).name("User").email("user@user.ru").build();
        List<Item> listItem = List.of(Item.builder().id(1L).owner(user).name("Item").description("Item items").available(true).request(null).build());
        List<ItemDto> listItemDto = List.of(ItemDto.builder().id(1L).name("Item").description("Item items").available(true).requestId(null).build());
        List<ItemRequest> listItemRequest = List.of(ItemRequest.builder().id(1L).description("Test").created(start).requestor(user).build());
        List<ItemRequestDto> listItemRequestDto = List.of(ItemRequestDto.builder().id(1L).description("Test").created(start).requestor(user.getId()).items(listItemDto).build());

        Mockito.when(userService.findById(anyLong()))
                .thenReturn(user);
        Mockito.when(itemRequestRepository.findItemRequestByRequestor_Id(1L))
                .thenReturn(listItemRequest);
        Mockito.when(itemRepository.findItemByRequest_Id(anyLong()))
                .thenReturn(listItem);
        Mockito.when(userService.getById(anyLong()))
                .thenReturn(userDto);

        assertEquals(listItemRequestDto, itemRequestService.getRequest(1L));
    }

    @Test
    void getRequestAllTest() {
        LocalDateTime start = LocalDateTime.now();
        List<Item> listItem = List.of(Item.builder().id(1L).owner(user).name("Item").description("Item items").available(true).request(null).build());
        List<ItemDto> listItemDto = List.of(ItemDto.builder().id(1L).name("Item").description("Item items").available(true).requestId(null).build());
        List<ItemRequest> listItemRequest = List.of(ItemRequest.builder().id(1L).description("Test").created(start).requestor(user).build());
        List<ItemRequestDto> listItemRequestDto = List.of(ItemRequestDto.builder().id(1L).description("Test").created(start).requestor(user.getId()).items(listItemDto).build());

        Mockito.when(userService.getById(anyLong()))
                .thenReturn(userDto);
        Mockito.when(itemRequestRepository.findItemRequestByIdNotOrderByCreatedDesc(1L, Pageable.ofSize(4)))
                .thenReturn(listItemRequest);
        Mockito.when(itemRepository.findItemByRequest_Id(anyLong()))
                .thenReturn(listItem);

        assertEquals(listItemRequestDto, itemRequestService.getRequestAll(1L, 0, 4));
    }

    @Test
    void getRequestAllEmptyTest() {
        LocalDateTime start = LocalDateTime.now();
        User user = User.builder().id(1L).name("User").email("user@user.ru").build();
        List<Item> listItem = List.of(Item.builder().id(1L).owner(user).name("Item").description("Item items").available(true).request(null).build());
        List<ItemRequest> listItemRequest = List.of(ItemRequest.builder().id(1L).description("Test").created(start).requestor(user).build());

        Mockito.when(userService.getById(anyLong()))
                .thenReturn(userDto);
        Mockito.when(itemRequestRepository.findItemRequestByIdNotOrderByCreatedDesc(1L, Pageable.ofSize(4)))
                .thenReturn(listItemRequest);
        Mockito.when(itemRepository.findItemByRequest_Id(anyLong()))
                .thenReturn(listItem);

        assertEquals(List.of(), itemRequestService.getRequestAll(1L, null, 4));
    }

    @Test
    public void getByIdRequestTest() {
        Mockito.when(userService.getById(anyLong()))
                .thenReturn(userDto);
        Mockito.when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        userService.add(userDto);
        itemRequestService.addRequest(1L, requestDto);
        ItemRequestDto itemRequestResultDto = itemRequestService.getRequestId(1L, 1L);

        Assertions.assertEquals(itemRequestResultDto.getId(), 1);
    }
}