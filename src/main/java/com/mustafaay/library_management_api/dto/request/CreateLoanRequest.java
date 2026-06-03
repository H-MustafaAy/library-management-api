package com.mustafaay.library_management_api.dto.request;

import com.mustafaay.library_management_api.entity.Loan;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLoanRequest {
    //ödünç verilecek kitabın idsi
    @NotNull(message = "kitap id boş olamaz!")
    private Long bookId;


    //kitabı ödünç alacak üyenin idsi
    @NotNull(message = "üye id boş olamaz!")
    private Long memberId;


}
