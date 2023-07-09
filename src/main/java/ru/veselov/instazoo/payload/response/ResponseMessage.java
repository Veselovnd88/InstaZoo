package ru.veselov.instazoo.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseMessage {

    @Schema(description = "Information message", example = "Message sample")
    String message;
}
