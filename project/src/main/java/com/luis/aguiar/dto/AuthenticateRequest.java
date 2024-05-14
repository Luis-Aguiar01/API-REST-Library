package com.luis.aguiar.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticateRequest {
    private String email;
    private String password;
}
