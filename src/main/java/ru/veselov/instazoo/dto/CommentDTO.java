package ru.veselov.instazoo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import ru.veselov.instazoo.entity.PostEntity;

import java.time.LocalDateTime;

@Data
public class CommentDTO {

    private Long id;

    private String username;

    @NotEmpty
    private String message;

}