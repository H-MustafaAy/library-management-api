package com.mustafaay.library_management_api.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FineResponse {

    private Long fineId;

    private Long memberId;

    private String memberName;

    private Long loanId;

    private Long bookId;

    private String bookTitle;

    private BigDecimal amount;

    private boolean paid;

    private String reason;

    private LocalDate loanDate;

    private LocalDate dueDate;

    private LocalDate returnDate;

    private LocalDateTime createdAt;

    private LocalDateTime paidAt;
}