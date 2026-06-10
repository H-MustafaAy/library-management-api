package com.mustafaay.library_management_api.service;

import com.mustafaay.library_management_api.dto.request.CreateLoanRequest;
import com.mustafaay.library_management_api.dto.response.LoanResponse;
import com.mustafaay.library_management_api.entity.Book;
import com.mustafaay.library_management_api.entity.Loan;
import com.mustafaay.library_management_api.enums.LoanStatus;
import com.mustafaay.library_management_api.entity.Member;
import com.mustafaay.library_management_api.enums.MemberStatus;
import com.mustafaay.library_management_api.exception.BadRequestException;
import com.mustafaay.library_management_api.exception.ResourceNotFoundException;
import com.mustafaay.library_management_api.mapper.LoanMapper;
import com.mustafaay.library_management_api.repository.BookRepository;
import com.mustafaay.library_management_api.repository.FineRepository;
import com.mustafaay.library_management_api.repository.LoanRepository;
import com.mustafaay.library_management_api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mustafaay.library_management_api.entity.Fine;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {


    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final LoanMapper loanMapper;
    private final FineRepository fineRepository;

    private static final BigDecimal DAILY_FINE_AMOUNT = BigDecimal.valueOf(2);
    private static final int MAX_ACTIVE_LOAN_COUNT = 3;

    @Transactional
    public LoanResponse createLoan(CreateLoanRequest request){ //bookid , memberid

        Long memberId = request.getMemberId();
        Long bookId = request.getBookId();
        LocalDate todayDate = LocalDate.now();

        //üye var mı
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Üye bulunamadı."));

        //üye aktif mi
        if(member.getStatus() != MemberStatus.ACTIVE){
            throw new BadRequestException("Üye pasif durumda - pasif üyeler kitap ödünç alamaz");
        }
        //teslim tarihi geçmiş kitabı olan üye yeni kitap alamaz
        if(loanRepository.existsByMemberIdAndReturnDateIsNullAndDueDateBefore(memberId,todayDate)){
            throw new BadRequestException("Teslim tarihi geçmiş kitabı olan üye yeni kitap alamaz - Önce kitabı iade etmeli.");
        }
        //ödenmemiş cezası olan üye kitap alamaz.
            if(fineRepository.existsByMemberIdAndPaidFalseAndAmountGreaterThan(memberId, BigDecimal.ZERO)){
                throw new BadRequestException("Üyenin ödenmemiş cezası var. Ceza ödenmeden yeni kitap alınamaz.");
            }

        //bir üye aynı anda en fazla 3 kitap ödünç alabilir
        long activeLoanCount = loanRepository.countByMemberIdAndStatus(memberId, LoanStatus.BORROWED);

        if (activeLoanCount >= 3) {
            throw new BadRequestException("Bir üye aynı anda en fazla 3 kitap ödünç alabilir.");
        }

        //Üye elinde zaten olan aynı kitabı tekrar alamaz.
        if(loanRepository.existsByMemberIdAndBookIdAndStatus(memberId, bookId, LoanStatus.BORROWED)){
            throw new BadRequestException("Bu kitap şuan üyede mevcut");
        }


        //kitap var mı
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Kitap bulunamadı."));

        // kitap rafta var mı kontrol ediliyor
        if(book.getAvailableCopies()<=0){
            throw new BadRequestException("Bu kitabın ödünç verilebilir kopyası yok");
        }





        //loan nesnesini oluşturma
        Loan loan = Loan.builder()
                .book(book)
                .member(member)
                .build();

        // kitabın raftaki copya sayısını azaltır
        book.setAvailableCopies(book.getAvailableCopies()-1);

        //loan kaydını veritabanına kaydeder
        Loan savedLoan = loanRepository.save(loan);

        //kaydedilen savedLoanı response çevirdim.
        LoanResponse response = loanMapper.toResponse(savedLoan);

        return response;
    }

    //veritabanından bütün loan kayıtlarını getirir.
    @Transactional(readOnly = true)
    public Page<LoanResponse> getAllLoans(Pageable pageable){

        Page<LoanResponse> responses = loanRepository.findAll(pageable)
                .map(loanMapper::toResponse);
       return responses;
    }
    //id ile ödünç kaydı döndürme
    @Transactional(readOnly = true)
    public LoanResponse getLoanById(Long id) {

        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ödünç kaydı bulunamadı."));
        LoanResponse response = loanMapper.toResponse(loan);

        return response;
    }

    //üye id sine göre ödünç kaydı getirme
    @Transactional(readOnly = true)
    public Page<LoanResponse> getLoansByMemberId(Long memberId, Pageable pageable) {

        return loanRepository.findByMemberId(memberId, pageable)
                .map(loanMapper::toResponse);
    }
    //kitap id sine göre ödünç kaydı getirme
    @Transactional(readOnly = true)
    public List<LoanResponse> getLoansByBookId(Long bookId) {

        List<LoanResponse> responses = loanRepository.findByBookId(bookId)
                .stream()
                .map(loanMapper::toResponse)
                .toList();

        return responses;
    }

    //kitabın durumuna göre
    @Transactional(readOnly = true)
    public List<LoanResponse> getLoansByStatus(LoanStatus status) {

        List<LoanResponse> responses = loanRepository.findByStatus(status)
                .stream()
                .map(loanMapper::toResponse)
                .toList();

        return responses;
    }
    //iade edilmemiş kayıtları
    @Transactional(readOnly = true)
    public List<LoanResponse> getBorrowedLoans() {

        return loanRepository.findByStatus(LoanStatus.BORROWED)
                .stream()
                .map(loanMapper::toResponse)
                .toList();
    }
    //iade edilmiş kayıtları
    @Transactional(readOnly = true)
    public List<LoanResponse> getReturnedLoans() {

        return loanRepository.findByStatus(LoanStatus.RETURNED)
                .stream()
                .map(loanMapper::toResponse)
                .toList();
    }

    //süresi geçmiş ödünçleri getirir
    @Transactional(readOnly = true)
    public Page<LoanResponse> getOverdueLoans(Pageable pageable) {

        return loanRepository.findByStatus(LoanStatus.OVERDUE,pageable)
                .map(loanMapper::toResponse);
    }

    @Transactional
    public void updateOverdueLoans() {

        LocalDate today = LocalDate.now();

        //teslim tarihi geçmiş ama hala BORROWED olan kayıtları bulur
        List<Loan> overdueLoans = loanRepository.findByDueDateBeforeAndStatus(
                today,
                LoanStatus.BORROWED
        );

        //her bir kaydın durumunu OVERDUE yap
        for (Loan loan : overdueLoans) {
            loan.setStatus(LoanStatus.OVERDUE);
        }

        //güncellenen kayıtları veritabanına kaydet
        loanRepository.saveAll(overdueLoans);
    }

    @Transactional
    public LoanResponse returnLoan(Long id){

        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ödünç kaydı bulunamadı."));

        if(loan.getStatus()==LoanStatus.RETURNED){
            throw new BadRequestException("Ödünç kaydı   zaten iade edilmiş.");
        }

        //kitabı al
        Book book = loan.getBook();

        LocalDate returnDate = LocalDate.now();

        //iade tarihini bugünün tarihi yap
        loan.setReturnDate(returnDate);

        //ödünç durumunu returned yaptım
        loan.setStatus(LoanStatus.RETURNED);

         // ceza hesaplanıyor
        BigDecimal fineAmount = calculateFine(loan.getDueDate(), returnDate);

        loan.setFineAmount(fineAmount);

        if (fineAmount.compareTo(BigDecimal.ZERO) > 0) {
            loan.setFinePaid(false);

            Fine fine = Fine.builder()
                    .member(loan.getMember())
                    .loan(loan)
                    .amount(fineAmount)
                    .paid(false)
                    .reason("Geç iade cezası")
                    .createdAt(LocalDateTime.now())
                    .build();

            fineRepository.save(fine);
        } else {
            loan.setFinePaid(true);
        }

        //kitap geriye döndüğü için müsait kopya sayısını 1 arttırdım
        book.setAvailableCopies(book.getAvailableCopies()+1);

        Loan updatedLoan = loanRepository.save(loan);

        //response çevirerek geri döndüm.
        return loanMapper.toResponse(updatedLoan);
    }

    @Transactional
    public void deleteLoan(Long id){
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ödünç kaydı bulunamadı."));

        if (loan.getStatus() != LoanStatus.RETURNED) {
            throw new BadRequestException("Sadece iade edilmiş ödünç kayıtları silinebilir.-BORROWED ve OVERDUE durumdakiler silinemez.-");
        }
        loanRepository.delete(loan);
    }

    //ceza hesaplama metodu
    private BigDecimal calculateFine(LocalDate dueDate, LocalDate returnDate) {

        if (!returnDate.isAfter(dueDate)) {
            return BigDecimal.ZERO;
        }

        long lateDays = ChronoUnit.DAYS.between(dueDate, returnDate);

        return DAILY_FINE_AMOUNT.multiply(BigDecimal.valueOf(lateDays));
    }

    //ceza ödemek için
    @Transactional
    public LoanResponse payFine(Long id) {

        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ödünç kaydı bulunamadı."));

        if (loan.getFineAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Bu ödünç kaydına ait ceza bulunmamaktadır.");
        }

        if (loan.getFinePaid()) {
            throw new BadRequestException("Bu ceza zaten ödenmiş.");
        }

        loan.setFinePaid(true);

        Fine fine = fineRepository.findByLoanId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bu ödünç kaydına ait ceza kaydı bulunamadı."));

        fine.setPaid(true);
        fine.setPaidAt(LocalDateTime.now());

        Loan updatedLoan = loanRepository.save(loan);

        return loanMapper.toResponse(updatedLoan);
    }
}

