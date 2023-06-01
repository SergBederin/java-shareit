package ru.practicum.shareit.comment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentJsonTest {
    @Autowired
    private JacksonTester<CommentDto> jsonCommentShort;

    @Test
    void testCommentShort() throws IOException {
        LocalDateTime end = LocalDateTime.of(2025, 5, 25, 15, 15, 13);
        CommentDto commentDto = CommentDto.builder().text("Text test").created(end).build();

        JsonContent<CommentDto> result = jsonCommentShort.write(commentDto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Text test");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(end.toString());
    }
}
