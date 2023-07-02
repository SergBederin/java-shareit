package ru.practicum.shareit.booking;

public enum BookingStatus {
    WAITING("WAITING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    public final String label;

    BookingStatus(String label) {
        this.label = label;
    }
}
