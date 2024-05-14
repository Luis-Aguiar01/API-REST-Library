package com.luis.aguiar.exceptions;

import jakarta.validation.constraints.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.Instant;

@NoArgsConstructor @AllArgsConstructor
@Setter @Getter @ToString
public class ErrorModel {

    @NotNull
    private Instant timestamp;

    @NotNull
    private Integer status;

    @NotBlank
    private String error;

    @NotBlank
    private String message;

    @NotBlank
    private String path;
}