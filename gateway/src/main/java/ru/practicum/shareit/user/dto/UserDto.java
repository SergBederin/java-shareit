package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.Marker;

import javax.validation.constraints.*;

@Data
@Builder
public class UserDto {
    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class)
    private Long id;
    @NotBlank
    @NotEmpty
    private String name;
    @NotBlank
    @Email
    @NotNull(groups = Marker.OnUpdate.class)
    private String email;
}
