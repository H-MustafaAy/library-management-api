package com.mustafaay.library_management_api.controller;

import com.mustafaay.library_management_api.dto.request.CreateFineRequest;
import com.mustafaay.library_management_api.dto.response.FineResponse;
import com.mustafaay.library_management_api.dto.response.MemberFineTotalResponse;
import com.mustafaay.library_management_api.service.FineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fines")
@RequiredArgsConstructor
public class FineController {

    private final FineService fineService;

    //manuel ceza oluşturur
    @PostMapping
    public FineResponse createFine(@RequestBody CreateFineRequest request) {
        return fineService.createFine(request);
    }

    //üyenin tüm cezalarını listeler
    @GetMapping(path = "/member/{memberId}")
    public Page<FineResponse> getFinesByMemberId(@PathVariable Long memberId, Pageable pageable) {
        return fineService.getFinesByMemberId(memberId, pageable);
    }

    //üyenin sadece ödenmemiş cezalarını listeler
    @GetMapping(path = "/member/{memberId}/unpaid")
    public List<FineResponse> getUnpaidFinesByMemberId(@PathVariable Long memberId) {
        return fineService.getUnpaidFinesByMemberId(memberId);
    }

    //üyenin toplam ödenmemiş borcunu getirir
    @GetMapping(path = "/member/{memberId}/total")
    public MemberFineTotalResponse getTotalUnpaidFineByMemberId(@PathVariable Long memberId) {
        return fineService.getTotalUnpaidFineByMemberId(memberId);
    }

    //cezayı ödendi olarak işaretler
    @PutMapping(path = "/{fineId}/pay")
    public FineResponse payFine(@PathVariable Long fineId) {
        return fineService.payFine(fineId);
    }
}