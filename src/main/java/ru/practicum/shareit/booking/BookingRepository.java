package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, CrudRepository<Booking, Long> {
    @Query(value = "select b.* from bookings as b " +
            "left join items as i on b.item_id = i.id " +
            "where b.id = ?1 and (b.booker_id = ?2 or i.owner_id = ?2) " +
            "group by b.id", nativeQuery = true)
    Booking getByIdBooking(Long bookingId, Long bookerId);

    //List<Booking> findAllBookingByBookerId(Long userId);
    @Query(value = "select b " +
            "from Booking b " +
            "join Item i on i.id=b.item " +
            "and b.booker.id=?1 and b.booker.id <> i.owner "
            + "order by b.start desc")
    List<Booking> getBookingByBookerIdAll(Long userId);


    @Query(value = "select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "join fetch b.booker as u " +
            "where b.booker.id = ?1 and ?2 between b.start and b.end " +
            "order by b.end desc")
    List<Booking> getBookingByUserIdAndBookingStatusCurrent(Long userId, LocalDateTime time);

    @Query(value = "select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "join fetch b.booker as u " +
            "where b.booker.id = ?1 and b.end < ?2 " +
            "order by b.end desc")
    List<Booking> getBookingByUserIdAndBookingStatusPast(Long userId, LocalDateTime time);

    @Query(value = "select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "join fetch b.booker as u " +
            "where b.booker.id = ?1 and b.start > ?2 " +
            "order by b.end desc")
    List<Booking> getBookingByUserIdAndBookingStatusFuture(Long userId, LocalDateTime time);

    @Query(value = "select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "join fetch b.booker as u " +
            "where b.booker.id = ?1 and b.bookingStatus like ?2 " +
            "order by b.end desc")
    List<Booking> getBookingByUserIdAndBookingStatusWaiting(Long userId, BookingStatus bookingStatus);

    @Query(value = "select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "join fetch b.booker as u " +
            "where b.booker.id = ?1 and b.bookingStatus like ?2 " +
            "order by b.end desc")
    List<Booking> getBookingByUserIdAndBookingStatusRejected(Long userId, BookingStatus bookingStatus);

    @Query(value = "select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "join fetch b.booker as u " +
            "where i.owner.id = ?1 " +
            "order by b.start desc")
    List<Booking> getBookingByOwnerIdAll(Long userId);

    @Query(value = "select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "join fetch b.booker as u " +
            "where i.owner.id = ?1 and ?2 between b.start and b.end " +
            "order by b.end desc")
    List<Booking> getByItemOwnerIdAndBookingStatusCurrent(Long userId, LocalDateTime time);

    @Query(value = "select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "join fetch b.booker as u " +
            "where i.owner.id = ?1 and b.end < ?2 " +
            "order by b.end desc")
    List<Booking> getByItemOwnerIdAndBookingStatusPast(Long userId, LocalDateTime time);

    @Query(value = "select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "join fetch b.booker as u " +
            "where i.owner.id = ?1 and b.start > ?2 " +
            "order by b.end desc")
    List<Booking> getBookingByItemOwnerIdAndBookingStatusFuture(Long userId, LocalDateTime time);

    @Query(value = "select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "join fetch b.booker as u " +
            "where i.owner.id = ?1 and b.bookingStatus like ?2 " +
            "order by b.end desc")
    List<Booking> getBookingByItemOwnerIdAndBookingStatusWaiting(Long userId, BookingStatus bookingStatus);

    @Query(value = "select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "join fetch b.booker as u " +
            "where i.owner.id = ?1 and b.bookingStatus like ?2 " +
            "order by b.end desc")
    List<Booking> getBookingByItemOwnerIdAndBookingStatusRejected(Long userId, BookingStatus bookingStatus);

    @Query(value = "select b.* from bookings as b " +
            "left join items as i on b.item_id = i.id " +
            "left join users as u on i.owner_id = u.id " +
            "where i.owner_id = ?1 and i.id = ?2 and b.booking_status like 'APPROVED' and b.start_date <= ?3 " +
            "order by b.start_date desc " +
            "limit 1", nativeQuery = true)
    Booking findByItemIdLast(Long userId, Long itemId, LocalDateTime time);

    @Query(value = "select b.* from bookings as b " +
            "left join items as i on b.item_id = i.id " +
            "left join users as u on i.owner_id = u.id " +
            "where i.owner_id = ?1 and i.id = ?2 and b.booking_status like 'APPROVED' and b.start_date > ?3 " +
            "order by b.start_date asc " +
            "limit 1", nativeQuery = true)
    Booking findByItemIdNext(Long userId, Long itemId, LocalDateTime time);

    @Query(value = "select b.* from bookings as b " +
            "left join items as i on b.item_id = i.id " +
            "left join users as u on i.owner_id = u.id " +
            "where b.booker_id = ?1 and i.id = ?2 and i.owner_id != ?1 and b.start_date < ?3 " +
            "order by b.end_date desc " +
            "limit 1", nativeQuery = true)
    Booking getBookingByBookerIdAndItemId(Long userId, Long itemId, LocalDateTime time);
}