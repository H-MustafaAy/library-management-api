package com.mustafaay.library_management_api.dto.response;

import com.mustafaay.library_management_api.enums.LoanStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class LoanResponse {

    private Long id;

    //kitabın bilgileri
    private Long bookId;
    private String bookTitle;
    private String isbn;

    // üyenin bilgileri
    private Long memberId;
    private String memberFullName;
    private String memberEmail;


    // ödünç bilgileri
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    private LoanStatus status;

    private BigDecimal fineAmount;
    private Boolean finePaid;
}


