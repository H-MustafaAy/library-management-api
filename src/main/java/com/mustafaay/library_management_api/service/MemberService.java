package com.mustafaay.library_management_api.service;


import com.mustafaay.library_management_api.dto.request.CreateMemberRequest;
import com.mustafaay.library_management_api.dto.request.UpdateMemberRequest;
import com.mustafaay.library_management_api.dto.response.MemberResponse;
import com.mustafaay.library_management_api.entity.Member;
import com.mustafaay.library_management_api.enums.MemberStatus;
import com.mustafaay.library_management_api.exception.BadRequestException;
import com.mustafaay.library_management_api.exception.ResourceNotFoundException;
import com.mustafaay.library_management_api.mapper.MemberMapper;
import com.mustafaay.library_management_api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    public MemberResponse createMember(CreateMemberRequest request) {

        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Bu email adresi ile kayıtlı bir üyemiz var.");
        }

        //gelen CreateMemberRequesti veritabanına kayıt için gerekli member türüne çevirdim
        // membermapper daki methodumu kullandım
        Member member = memberMapper.toEntity(request);
        //veritabanıa kayıt ettim
        Member savedMember = memberRepository.save(member);

        //kullanıcıya gösterilmesi için savedmemberı response türüne döndürdüm
        MemberResponse response = memberMapper.toResponse(savedMember);
        return response;
    }

    //TÜM ÜYELERİ LİSTELEME
    @Transactional(readOnly = true)
    public List<MemberResponse> getAllMembers(){
        List<MemberResponse> allMembers = memberRepository.findAll()
                .stream()
                .map(memberMapper::toResponse)
                .toList();
        return allMembers;
    }
    //ÜYE NUMARASINA GÖRE ÜYE LİSTELEME
    @Transactional(readOnly = true)
    public MemberResponse getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Üye bulunamadı"));

        MemberResponse response = memberMapper.toResponse(member);
        return response;
    }

    //EMAİLE GÖRE ÜYE LİSTELEME
    @Transactional(readOnly = true)
    public MemberResponse getMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Bu email adresine sahip üye bulunamadı"));

        return memberMapper.toResponse(member);
    }
    //AD VEYA SOYADA GÖRE ÜYE LİSTELEME
    @Transactional(readOnly = true)
    public List<MemberResponse> searchMembers(String keyword) {
        return memberRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(memberMapper::toResponse)
                .toList();
    }

    //AKTİF ÜYELERİ LİSTELEME
    @Transactional(readOnly = true)
    public List<MemberResponse> getActiveMembers() {
        return memberRepository.findByStatus(MemberStatus.ACTIVE)
                .stream()
                .map(memberMapper::toResponse)
                .toList();
    }

    //PASİF ÜYELERİ LİSTELEME
    @Transactional(readOnly = true)
    public List<MemberResponse> getPassiveMembers() {
        return memberRepository.findByStatus(MemberStatus.PASSIVE)
                .stream()
                .map(memberMapper::toResponse)
                .toList();
    }

    // ÜYE GÜNCELLEME
    @Transactional
    public MemberResponse updateMember(Long id, UpdateMemberRequest request) {
        Member member = findMemberById(id);

        boolean emailChanged = !member.getEmail().equals(request.getEmail());

        if (emailChanged && memberRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Bu email adresi başka bir üyeye ait");
        }

        memberMapper.updateEntity(member, request);

        Member updatedMember = memberRepository.save(member);

        return memberMapper.toResponse(updatedMember);
    }

    //AKTİF ÜYEYİ PASİF DURUMA GETİRME
    @Transactional
    public MemberResponse deactivateMember(Long id) {
        Member member = findMemberById(id);

        member.setStatus(MemberStatus.PASSIVE);

        Member updatedMember = memberRepository.save(member);

        return memberMapper.toResponse(updatedMember);
    }

    //ÜYE SİLME
    @Transactional
    public void deleteMember(Long id) {
        Member member = findMemberById(id);

        memberRepository.delete(member);
    }

    //İD NUMARASI İLE ÜYE BULMA
    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Üye bulunamadı"));
    }
}