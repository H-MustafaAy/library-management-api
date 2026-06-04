package com.mustafaay.library_management_api.mapper;

import com.mustafaay.library_management_api.dto.response.LoanResponse;
import com.mustafaay.library_management_api.entity.Loan;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {


    //burada loan entitysini  dışarıya döneceğimiz formata çevirdik
    public LoanResponse toResponse(Loan loan){
        return LoanResponse.builder()
                .id(loan.getId())

                //book
                .bookId(loan.getBook().getId())
                .bookTitle(loan.getBook().getTitle())
                .isbn(loan.getBook().getIsbn())

                //üye
                .memberId(loan.getMember().getId())
                .memberFullName(loan.getMember().getFirstName()+ " "+ loan.getMember().getLastName())
                .memberEmail(loan.getMember().getEmail())


                //ödünç bilgileri
                .loanDate(loan.getLoanDate())
                .dueDate(loan.getDueDate())
                .returnDate(loan.getReturnDate())
                .status(loan.getStatus())
                .fineAmount(loan.getFineAmount())
                .finePaid(loan.getFinePaid())


                .build();
    }
}
