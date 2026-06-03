package com.mustafaay.library_management_api.repository;

import com.mustafaay.library_management_api.entity.Loan;
import com.mustafaay.library_management_api.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan,Long> {

    //member ıd si verilene üyenin tüm ödünç kayıtları gelir.
    List<Loan> findByMemberId(Long memberId);

    //id si verilen kitabın tüm ödünç kayıtları getilir
    List<Loan> findByBookId(Long bookId);

    // ödünç durumuna göre kayıtları getirir
    // BORROWED, RETURNED, OVERDUE
    List<Loan> findByStatus(LoanStatus status);

    //belirli bir üyeye ait belirli durumdaki ödünç kayıtlarını getirir
    List<Loan> findByMemberIdAndStatus(Long memberId, LoanStatus status);

    //belirli bir kitaba ait belirli durumdaki ödünç kayıtlarını getirir
    List<Loan> findByBookIdAndStatus(Long bookId, LoanStatus status);

    //son teslim tarihi geçmiş , iade edilmemiş ödünçleri getirir
    List<Loan> findByDueDateBeforeAndStatus(LocalDate date, LoanStatus status);

    //bir kitabın şuanda ödünçte olup olmadığını kontrol eder
    boolean existsByBookIdAndStatus(Long bookId, LoanStatus status);
}
