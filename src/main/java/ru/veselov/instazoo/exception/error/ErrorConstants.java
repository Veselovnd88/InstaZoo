package ru.veselov.instazoo.exception.error;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorConstants {

    public static final String ERROR_NOT_AUTHORIZED = "error.not.authorized";

    public static final String ERROR_CONFLICT = "error.conflict";

    public static final String ERROR_VALIDATION = "error.validation";

    public static final String ERROR_NOT_FOUND = "error.not.found";

    public static final String SERVER_ERROR = "error.server";

}