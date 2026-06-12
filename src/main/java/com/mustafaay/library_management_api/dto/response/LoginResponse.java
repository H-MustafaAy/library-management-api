package com.mustafaay.library_management_api.dto.response;

import com.mustafaay.library_management_api.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String tokenType;
    private Long memberId;
    private String email;
    private String fullName;
    private Role role;
}