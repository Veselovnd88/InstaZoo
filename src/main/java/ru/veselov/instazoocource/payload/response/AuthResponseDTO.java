package ru.veselov.instazoocource.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {

    private boolean success;

    private String token;

}