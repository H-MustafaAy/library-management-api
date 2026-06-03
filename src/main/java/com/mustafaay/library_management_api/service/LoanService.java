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
import com.mustafaay.library_management_api.repository.LoanRepository;
import com.mustafaay.library_management_api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {


    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final LoanMapper loanMapper;

    @Transactional
    public LoanResponse createLoan(CreateLoanRequest request){

        //kitap var mı
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Kitap bulunamadı."));
        //üye var mı
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Üye bulunamadı."));

        //üye aktif mi

        if(member.getStatus() != MemberStatus.ACTIVE){
            throw new BadRequestException("Üye pasif durumda - pasif üyeler kitap ödünç alamaz");
        }

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
    public List<LoanResponse> getAllLoans(){

        List<LoanResponse> responses = loanRepository.findAll()
                .stream()
                .map(loanMapper::toResponse)
                .toList();
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
    public List<LoanResponse> getLoansByMemberId(Long memberId) {

        List<LoanResponse> responses = loanRepository.findByMemberId(memberId)
                .stream()
                .map(loanMapper::toResponse)
                .toList();

        return responses;
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
    public List<LoanResponse> getOverdueLoans() {

        return loanRepository.findByDueDateBeforeAndStatus(
                        LocalDate.now(),
                        LoanStatus.BORROWED
                )
                .stream()
                .map(loanMapper::toResponse)
                .toList();
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

        //iade tarihini bugünün tarihi yap
        loan.setReturnDate(LocalDate.now());

        //ödünç durumunu returned yaptım
        loan.setStatus(LoanStatus.RETURNED);

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
}
