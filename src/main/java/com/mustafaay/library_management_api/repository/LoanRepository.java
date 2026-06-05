package com.mustafaay.library_management_api.repository;

import com.mustafaay.library_management_api.entity.Loan;
import com.mustafaay.library_management_api.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
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

    //son teslim tarihi geçmiş , iade edilmemiş ödünçleri getirir
    List<Loan> findByDueDateBeforeAndStatus(LocalDate date, LoanStatus status);

    //üyeleri silmeden önce bu kontrol yapılmalı
    //üyenin iade edilmemiş kitabı var mı
    boolean existsByMemberIdAndReturnDateIsNull(Long memberId);

    //kitap silmeden bu kayıt kesinlikle yapılmalı
    //kitabın ödünçte olan kopyası var mı
    boolean existsByBookIdAndReturnDateIsNull(Long bookId);

    //üyenin süresi geçmiş ama iade edilmemiş kitabı var mı
    boolean existsByMemberIdAndReturnDateIsNullAndDueDateBefore(Long memberId, LocalDate today);

    //bir üyenin aktif ödünç kitap sayısı
    long countByMemberIdAndStatus(Long memberId, LoanStatus status);

    //aynı kitabın aynı üyede aktif olarak bulunup bulunmadığını kontrol eder
    boolean existsByMemberIdAndBookIdAndStatus(Long memberId, Long bookId, LoanStatus status);
}
