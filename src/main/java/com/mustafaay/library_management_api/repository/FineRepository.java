package com.mustafaay.library_management_api.repository;

import com.mustafaay.library_management_api.entity.Fine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface FineRepository extends JpaRepository<Fine, Long> {

    //üyenin ödenmemiş cezası var mı
    boolean existsByMemberIdAndPaidFalseAndAmountGreaterThan(Long memberId, BigDecimal amount);

    //aynı ödünç kaydı için daha önce ceza oluşturulmuş mu
    boolean existsByLoanId(Long loanId);

    //üyenin tüm cezalarını getirir
    Page<Fine> findByMemberId(Long memberId , Pageable pageable);

    //üyenin sadece ödenmemiş cezalarını getirir
    List<Fine> findByMemberIdAndPaidFalse(Long memberId);

    Optional<Fine> findByLoanId(Long loanId);

    //üyenin toplam ödenmemiş borcunu getirir
    @Query("""
            SELECT COALESCE(SUM(f.amount), 0)
            FROM Fine f
            WHERE f.member.id = :memberId
              AND f.paid = false
            """)
    BigDecimal getTotalUnpaidFineByMemberId(Long memberId);
}