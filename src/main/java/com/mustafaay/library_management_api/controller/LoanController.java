package com.mustafaay.library_management_api.controller;

import com.mustafaay.library_management_api.dto.request.CreateLoanRequest;
import com.mustafaay.library_management_api.dto.response.LoanResponse;
import com.mustafaay.library_management_api.enums.LoanStatus;
import com.mustafaay.library_management_api.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    //yeni ödünç kaydı oluşturma
    @PostMapping(path = "create")
    public LoanResponse createLoan(@Valid @RequestBody CreateLoanRequest request){
        LoanResponse response = loanService.createLoan(request);
        return response;
    }

    //tüm loanları getirir.
    @GetMapping(path = "/list")
    public List<LoanResponse> getAllLoan(){
        return loanService.getAllLoans();
    }
    // sadece id numarası verilen loan ı getirir.
    @GetMapping(path = "/list/{id}")
    public LoanResponse getLoanById(@PathVariable Long id){
        return loanService.getLoanById(id);
    }

    //ÜYE id sine göre ödünç kaydı getirir
    @GetMapping(path = "/member/{memberId}")
    public List<LoanResponse> getLoansByMemberId(@PathVariable Long memberId){
        return loanService.getLoansByMemberId(memberId);
    }

    //kitab id ye göre ödünç kaydı getirir
    @GetMapping(path = "/book/{bookId}")
    public List<LoanResponse> getLoansByBookId(@PathVariable Long bookId){
        return loanService.getLoansByBookId(bookId);
    }

    //duruma göre ödünç kaydı listeler  /api/loans/status/BORROWED
    @GetMapping(path = "/status/{status}")
    public List<LoanResponse> getLoansByStatus(@PathVariable LoanStatus status){
        return loanService.getLoansByStatus(status);
    }

    //aktif ödünç kayıtlarını listeler
    @GetMapping("/borrowed")
    public List<LoanResponse> getBorrowedLoans() {
        return loanService.getBorrowedLoans();
    }
    //iade edilmiş ödünç kayıtlarını listeler
    @GetMapping("/returned")
    public List<LoanResponse> getReturnedLoans() {
        return loanService.getReturnedLoans();
    }
    //süresi geçmiş ödünç kayıtlarını listeler
    @GetMapping("/overdue")
    public List<LoanResponse> getOverdueLoans() {
        return loanService.getOverdueLoans();
    }

    //kitap iade işlemi
    @PatchMapping("/{id}/return")
    public LoanResponse returnLoan(@PathVariable Long id) {
        return loanService.returnLoan(id);
    }

    //ödünç kaydı silme
    @DeleteMapping("/{id}")
    public void deleteLoan(@PathVariable Long id) {
        loanService.deleteLoan(id);
    }
}
