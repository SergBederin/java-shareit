package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;
    @MockBean
    CommentService commentService;

    @Autowired
    private MockMvc mvc;

    @Test
    void getByUserIdTest() throws Exception {
        LocalDateTime end = LocalDateTime.now().plusMinutes(1);
        User user = User.builder().id(1L).name("User").email("user@user.ru").build();
        User owner = User.builder().id(2L).name("Owner").email("owner@user.ru").build();
        Item item = Item.builder().owner(owner).name("Item").description("Item items").available(true).request(null).build();
        List<CommentDto> listCommentDto = List.of(CommentDto.builder().text("Text test").authorName(user.getName()).created(end).build());
        List<ItemDtoWithBookingAndComments> itemDtoWithBookingAndCommentsList = List.of(ItemDtoWithBookingAndComments.builder().id(1L).owner(owner.getId()).description("Item items").available(true).comments(listCommentDto).name(item.getName()).build());

        when(itemService.getAllItemByUser(any(), any(), any()))
                .thenReturn(itemDtoWithBookingAndCommentsList);

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDtoWithBookingAndCommentsList)));
    }

    @Test
    void getByItemIdTest() throws Exception {
        LocalDateTime end = LocalDateTime.of(2023, 5, 27, 14, 50, 11);
        User user = User.builder().id(1L).name("User").email("user@user.ru").build();
        User owner = User.builder().id(2L).name("Owner").email("owner@user.ru").build();
        Item item = Item.builder().owner(owner).name("Item").description("Item items").available(true).request(null).build();
        List<CommentDto> listCommentDto = List.of(CommentDto.builder().text("Text test").authorName(user.getName()).created(end).build());
        ItemDtoWithBookingAndComments itemDtoWithBookingAndComments = ItemDtoWithBookingAndComments.builder().id(1L).owner(owner.getId()).name(item.getName()).description("Item items").available(true).comments(listCommentDto).build();

        when(itemService.getByItemId(anyLong(), anyLong()))
                .thenReturn(itemDtoWithBookingAndComments);

        mvc.perform(get("/items/1")
                        .content(mapper.writeValueAsString(itemDtoWithBookingAndComments))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoWithBookingAndComments.getId()), Long.class))
                .andExpect(jsonPath("$.owner", is(itemDtoWithBookingAndComments.getOwner().intValue())))
                .andExpect(jsonPath("$.name", is(itemDtoWithBookingAndComments.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoWithBookingAndComments.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoWithBookingAndComments.getAvailable())))
                .andExpect(jsonPath("$.request", is(itemDtoWithBookingAndComments.getRequest())))
                .andExpect(jsonPath("$.lastBooking", is(itemDtoWithBookingAndComments.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemDtoWithBookingAndComments.getNextBooking())))
                .andExpect(jsonPath("$.comments", notNullValue()));

    }

    @Test
    void searchItemTest() throws Exception {
        List<ItemDto> listItemDto = List.of(ItemDto.builder().id(1L).name("Item").description("Item items").available(true).requestId(null).build());
        when(itemService.search(any(), anyInt(), anyInt()))
                .thenReturn(listItemDto);

        mvc.perform(get("/items/search?text=a&from=0&size=4")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(listItemDto)));
    }

    @Test
    void addItemTest() throws Exception {
        ItemDto itemDto = ItemDto.builder().name("Item").description("Item items").available(true).requestId(null).build();
        when(itemService.add(any(), anyLong()))
                .thenReturn(itemDto);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                //.andExpect(jsonPath("$.owner", is(itemDto.getOwner()), Long.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));
    }

    @Test
    void updateTest() throws Exception {
        ItemDto itemDtoUpdate = ItemDto.builder().name("Update").description("Update").available(false).requestId(null).build();
        when(itemService.update(anyLong(), anyLong(), any()))
                .thenReturn(itemDtoUpdate);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDtoUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoUpdate.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoUpdate.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoUpdate.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoUpdate.getAvailable())))
                //.andExpect(jsonPath("$.owner", is(itemDtoUpdate.getOwner()), Long.class))
                .andExpect(jsonPath("$.requestId", is(itemDtoUpdate.getRequestId())));
    }

    @Test
    void addCommentTest() throws Exception {
        LocalDateTime start = LocalDateTime.of(2023, 5, 27, 14, 49, 11);
        User user = User.builder().id(1L).name("User").email("user@user.ru").build();
        CommentDto commentDto = CommentDto.builder().text("Text test").authorName(user.getName()).created(start).build();

        when(commentService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().toString())));
    }
}