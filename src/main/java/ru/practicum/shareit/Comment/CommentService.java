package ru.practicum.shareit.Comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.Comment.dto.CommentDto;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        Booking booking = bookingRepository.getBookingByBookerIdAndItemId(userId, itemId, LocalDateTime.now());
        if ((booking != null) && !booking.getBookingStatus().equals(BookingStatus.REJECTED)) {
            CommentDto comment = CommentDto.builder().text(commentDto.getText()).authorName(booking.getBooker().getName()).created(commentDto.getCreated()).build();
            if (comment.getCreated() == null) {
                comment.setCreated(LocalDateTime.now());
            }
            log.info("Пользователь с id = {}, добавил комментарий к вещи с id = {} : {}. ", userId, itemId, comment);
            return CommentMapper.mapToCommentDto(commentRepository.save(CommentMapper.mapToComment(comment, booking.getBooker(), booking.getItem())));
        } else {
            throw new ValidationException("Бронирования на вещь с id =" + itemId + " у пользователя с id = " + userId + " не найдена.");
        }
    }

    public List<CommentDto> getCommentsByItemId(Long itemId) {
        log.info("Запрошен комментарий к вещи с id = {}", itemId);
        return CommentMapper.mapToCommentDto(commentRepository.findCommentById(itemId));
    }
}
