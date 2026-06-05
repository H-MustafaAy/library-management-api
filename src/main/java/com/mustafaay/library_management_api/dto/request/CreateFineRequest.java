package com.mustafaay.library_management_api.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFineRequest {

    //ceza hangi ödünç almaya ait
    private Long loanId;

    //ceza miktarı
    private BigDecimal amount;

    //ceza açıklaması
    private String reason;
}