package com.mustafaay.library_management_api.dto.response;

import com.mustafaay.library_management_api.enums.MemberStatus;
import com.mustafaay.library_management_api.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MemberResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String address;

    private MemberStatus status;

    private Role role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}