package ru.practicum.shareit.Comment;

import lombok.Builder;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Comment.dto.CommentDto;
import ru.practicum.shareit.Comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
@Builder
public class CommentMapper {
    public static Comment mapToComment(CommentDto commentDto, User user, Item item) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .item(item)
                .user(user)
                .created(commentDto.getCreated())
                .build();
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getUser().getName())
                .created(comment.getCreated())
                .build();
    }

    public static List<CommentDto> mapToCommentDto(List<Comment> listComment) {
        List<CommentDto> commentDtoList = new ArrayList<>();
        for (Comment comment : listComment) {
            commentDtoList.add(mapToCommentDto(comment));
        }
        return commentDtoList;
    }
}