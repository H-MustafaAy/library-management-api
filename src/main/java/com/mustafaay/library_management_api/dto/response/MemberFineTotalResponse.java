package com.mustafaay.library_management_api.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberFineTotalResponse {

    private Long memberId;

    private String memberName;

    private BigDecimal totalUnpaidFine;
}