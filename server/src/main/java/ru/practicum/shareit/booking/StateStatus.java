package ru.practicum.shareit.booking;

public enum StateStatus {
    ALL("ALL"),
    CURRENT("CURRENT"),
    PAST("PAST"),
    FUTURE("FUTURE"),
    WAITING("WAITING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    public final String label;

    StateStatus(String label) {
        this.label = label;
    }
}
