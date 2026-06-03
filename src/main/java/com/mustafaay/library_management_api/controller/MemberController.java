package com.mustafaay.library_management_api.controller;

import com.mustafaay.library_management_api.dto.request.CreateMemberRequest;
import com.mustafaay.library_management_api.dto.request.UpdateMemberRequest;
import com.mustafaay.library_management_api.dto.response.MemberResponse;
import com.mustafaay.library_management_api.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/members")
@RequiredArgsConstructor
public class MemberController {

    public final MemberService memberService;

    // burada valid annotationı creatememberrequest içindeki validationları yapması için
    //yeni üye kaydı
    @PostMapping(path = "/create")
    public MemberResponse createMember(@Valid @RequestBody CreateMemberRequest request) {

        return memberService.createMember(request);
    }

    //tüm üyeleri listeler
    @GetMapping(path = "/list")
    public List<MemberResponse> getAllMembers() {
        return memberService.getAllMembers();
    }

    //id ile üye listeleme
    @GetMapping(path = "/{id}")
    public MemberResponse getMemberById(@PathVariable Long id) {
        return memberService.getMemberById(id);
    }

    //email ile listeleme
    @GetMapping(path = "email/{email}")
    public MemberResponse getMemberByEmail(@PathVariable String email){
        return memberService.getMemberByEmail(email);
    }

    //ad veya soyad ile arama yapar
    @GetMapping(path = "/search")
    public List<MemberResponse> searchMembers(@RequestParam String keyword){
        return memberService.searchMembers(keyword);
    }


    // active üyeleri getirir
    @GetMapping(path = "/active")
    public List<MemberResponse> getActiveMembers() {
        return memberService.getActiveMembers();
    }

    //passive üyeleri getirir
    @GetMapping(path = "/passive")
    public List<MemberResponse> getPassiveMembers() {
        return memberService.getPassiveMembers();
    }

    //güncelleme yapalır
    @PutMapping(path = "/{id}")
    public MemberResponse updateMember(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMemberRequest request
    ) {
        return memberService.updateMember(id, request);
    }


    //id si verilen üye passive duruma getirilir
    @PatchMapping("/deactivate/{id}")
    public MemberResponse deactivateMember(@PathVariable Long id) {
        return memberService.deactivateMember(id);
    }

    //üye silinir
    @DeleteMapping("/{id}")
    public void deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
    }
}
