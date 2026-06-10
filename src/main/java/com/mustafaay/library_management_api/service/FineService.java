package com.mustafaay.library_management_api.service;

import com.mustafaay.library_management_api.dto.request.CreateFineRequest;
import com.mustafaay.library_management_api.dto.response.FineResponse;
import com.mustafaay.library_management_api.dto.response.MemberFineTotalResponse;
import com.mustafaay.library_management_api.entity.Fine;
import com.mustafaay.library_management_api.entity.Loan;
import com.mustafaay.library_management_api.entity.Member;
import com.mustafaay.library_management_api.exception.BadRequestException;
import com.mustafaay.library_management_api.exception.ResourceNotFoundException;
import com.mustafaay.library_management_api.repository.FineRepository;
import com.mustafaay.library_management_api.repository.LoanRepository;
import com.mustafaay.library_management_api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FineService {

    private final FineRepository fineRepository;
    private final LoanRepository loanRepository;
    private final MemberRepository memberRepository;

    //manuel ceza oluşturur
    public FineResponse createFine(CreateFineRequest request) {

        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new ResourceNotFoundException("Ödünç kaydı bulunamadı."));

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Ceza miktarı 0'dan büyük olmalıdır.");
        }

        if (fineRepository.existsByLoanId(loan.getId())) {
            throw new BadRequestException("Bu ödünç kaydı için zaten ceza oluşturulmuş.");
        }

        Fine fine = Fine.builder()
                .member(loan.getMember())
                .loan(loan)
                .amount(request.getAmount())
                .paid(false)
                .reason(request.getReason() != null && !request.getReason().isBlank()
                        ? request.getReason()
                        : "Geç iade cezası")
                .createdAt(LocalDateTime.now())
                .build();

        Fine savedFine = fineRepository.save(fine);

        return mapToFineResponse(savedFine);
    }

    //üyenin tüm cezalarını getirir
    public Page<FineResponse> getFinesByMemberId(Long memberId, Pageable pageable) {

        memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Üye bulunamadı."));

        return fineRepository.findByMemberId(memberId, pageable)
                .map(this::mapToFineResponse);
    }

    //üyenin sadece ödenmemiş cezalarını getirir
    public List<FineResponse> getUnpaidFinesByMemberId(Long memberId) {

        memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Üye bulunamadı."));

        return fineRepository.findByMemberIdAndPaidFalse(memberId)
                .stream()
                .map(this::mapToFineResponse)
                .toList();
    }

    //üyenin toplam ödenmemiş borcunu getirir
    public MemberFineTotalResponse getTotalUnpaidFineByMemberId(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Üye bulunamadı."));

        BigDecimal totalFine = fineRepository.getTotalUnpaidFineByMemberId(memberId);

        return MemberFineTotalResponse.builder()
                .memberId(member.getId())
                .memberName(member.getFirstName() + " " + member.getLastName())
                .totalUnpaidFine(totalFine)
                .build();
    }

    //cezayı ödendi olarak işaretler
    public FineResponse payFine(Long fineId) {

        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new ResourceNotFoundException("Ceza kaydı bulunamadı."));

        if (fine.isPaid()) {
            throw new BadRequestException("Bu ceza zaten ödenmiş.");
        }

        fine.setPaid(true);
        fine.setPaidAt(LocalDateTime.now());

        Fine updatedFine = fineRepository.save(fine);

        return mapToFineResponse(updatedFine);
    }

    //fine entity sini FineResponse'a çevirir
    private FineResponse mapToFineResponse(Fine fine) {

        Loan loan = fine.getLoan();

        return FineResponse.builder()
                .fineId(fine.getId())
                .memberId(fine.getMember().getId())
                .memberName(fine.getMember().getFirstName() + " " + fine.getMember().getLastName())
                .loanId(loan.getId())
                .bookId(loan.getBook().getId())
                .bookTitle(loan.getBook().getTitle())
                .amount(fine.getAmount())
                .paid(fine.isPaid())
                .reason(fine.getReason())
                .loanDate(loan.getLoanDate())
                .dueDate(loan.getDueDate())
                .returnDate(loan.getReturnDate())
                .createdAt(fine.getCreatedAt())
                .paidAt(fine.getPaidAt())
                .build();
    }
}