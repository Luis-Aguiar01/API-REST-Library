package com.luis.aguiar.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticateRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
