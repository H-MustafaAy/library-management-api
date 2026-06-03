package com.mustafaay.library_management_api.entity;

import com.mustafaay.library_management_api.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "loans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //ödünç alınana kitap
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "book_id",nullable = false)
    private Book book;

    //kitabı ödünç alan üye
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = false)
    private Member member;

    //kitabın ödünç alındığı tarih
    @Column(nullable = false)
    private LocalDate loanDate;

    //kitabın son teslim tarihi
    @Column(nullable = false)
    private LocalDate dueDate;

    //kitabın teslim edildiği tarih
    @Column(nullable = true)
    private LocalDate returnDate;

    //ödünç durumu   BORROWED,
    //    RETURNED,
    //    OVERDUE
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    @PrePersist
    public void prePersist() {
        if (loanDate == null) {
            loanDate = LocalDate.now();
        }

        if (dueDate == null) {
            //ödünç süresini 14 gün olarak belirledim bunu buradan değiştirebiliriz
            dueDate = loanDate.plusDays(14);
        }

        if (status == null) {
            status = LoanStatus.BORROWED;
        }
    }
}
