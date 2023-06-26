package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;


import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;


    @Test
    void addRequestTest() throws Exception {
        LocalDateTime start = LocalDateTime.of(2024, 5, 27, 14, 49, 11);
        User user = User.builder().id(1L).name("User").email("user@user.ru").build();
        List<ItemDto> listItemDto = List.of(ItemDto.builder().id(1L).name("Item").description("Item items").available(true).requestId(null).build());
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().id(1L).description("Test").created(start).requestor(user.getId()).items(listItemDto).build();

        when(itemRequestService.addRequest(anyLong(), any()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestor", is(itemRequestDto.getRequestor()), Long.class))
                .andExpect(jsonPath("$.items", notNullValue()));
    }

    @Test
    void getRequestTest() throws Exception {
        LocalDateTime start = LocalDateTime.of(2024, 5, 27, 14, 49, 11);
        User user = User.builder().id(1L).name("User").email("user@user.ru").build();
        List<ItemDto> listItemDto = List.of(ItemDto.builder().id(1L).name("Item").description("Item items").available(true).requestId(null).build());
        List<ItemRequestDto> listItemRequestDto = List.of(ItemRequestDto.builder().id(1L).description("Test").created(start).requestor(user.getId()).items(listItemDto).build());

        when(itemRequestService.getRequest(anyLong()))
                .thenReturn(listItemRequestDto);

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(listItemRequestDto)));
    }

    @Test
    void getRequestAllTest()  throws Exception {
        LocalDateTime start = LocalDateTime.of(2024, 5, 27, 14, 49, 11);
        User user = User.builder().id(1L).name("User").email("user@user.ru").build();
        List<ItemDto> listItemDto = List.of(ItemDto.builder().id(1L).name("Item").description("Item items").available(true).requestId(null).build());
        List<ItemRequestDto> listItemRequestDto = List.of(ItemRequestDto.builder().id(1L).description("Test").created(start).requestor(user.getId()).items(listItemDto).build());

        when(itemRequestService.getRequestAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(listItemRequestDto);

        mvc.perform(get("/requests/all?from=0&size=4")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(listItemRequestDto)));
    }


    @Test
    void getRequestIdTest() throws Exception {
        LocalDateTime start = LocalDateTime.of(2024, 5, 27, 14, 49, 11);
        User user = User.builder().id(1L).name("User").email("user@user.ru").build();
        List<ItemDto> listItemDto = List.of(ItemDto.builder().id(1L).name("Item").description("Item items").available(true).requestId(null).build());
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().id(1L).description("Test").created(start).requestor(user.getId()).items(listItemDto).build();

        when(itemRequestService.getRequestId(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestor", is(itemRequestDto.getRequestor()), Long.class))
                .andExpect(jsonPath("$.items", notNullValue()));
    }
}

