package com.mustafaay.library_management_api.service;


import com.mustafaay.library_management_api.dto.request.CreateMemberRequest;
import com.mustafaay.library_management_api.dto.request.UpdateMemberRequest;
import com.mustafaay.library_management_api.dto.response.MemberResponse;
import com.mustafaay.library_management_api.entity.Member;
import com.mustafaay.library_management_api.enums.MemberStatus;
import com.mustafaay.library_management_api.enums.Role;
import com.mustafaay.library_management_api.exception.BadRequestException;
import com.mustafaay.library_management_api.exception.ResourceNotFoundException;
import com.mustafaay.library_management_api.mapper.MemberMapper;
import com.mustafaay.library_management_api.repository.LoanRepository;
import com.mustafaay.library_management_api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final LoanRepository loanRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberResponse createMember(CreateMemberRequest request) {

        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Bu email adresi ile kayıtlı bir üyemiz var.");
        }

        //gelen CreateMemberRequesti veritabanına kayıt için gerekli member türüne çevirdim
        // membermapper daki methodumu kullandım
        Member member = memberMapper.toEntity(request);

        member.setPassword(passwordEncoder.encode(request.getPassword()));

        //veritabanıa kayıt ettim
        Member savedMember = memberRepository.save(member);

        //kullanıcıya gösterilmesi için savedmemberı response türüne döndürdüm
        MemberResponse response = memberMapper.toResponse(savedMember);
        return response;
    }

    //TÜM ÜYELERİ LİSTELEME
    @Transactional(readOnly = true)
    public Page<MemberResponse> getAllMembers(Pageable pageable) {
        Page<MemberResponse> allMembers = memberRepository.findAll(pageable)
                .map(memberMapper::toResponse);
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

        // burada silinmek istenen üyenin iade etmediği kitap var mı diye kontrol yaptım
        if (loanRepository.existsByMemberIdAndReturnDateIsNull(id)) {
            throw new BadRequestException("İade edilmemiş kitabı bulunan üye silinemez!");
        }

        memberRepository.delete(member);
    }

    //İD NUMARASI İLE ÜYE BULMA
    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Üye bulunamadı"));
    }
    // librarian oluşturma
    public MemberResponse createLibrarian(CreateMemberRequest request) {

        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Bu email zaten kullanılıyor");
        }

        Member member = Member.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .status(MemberStatus.ACTIVE)
                .role(Role.LIBRARIAN)
                .build();

        Member savedMember = memberRepository.save(member);

        return memberMapper.toResponse(savedMember);
    }

    //admin oluşturma
    public MemberResponse createAdmin(CreateMemberRequest request) {

        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Bu email zaten kullanılıyor");
        }

        Member member = Member.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .status(MemberStatus.ACTIVE)
                .role(Role.ADMIN)
                .build();

        Member savedMember = memberRepository.save(member);

        return memberMapper.toResponse(savedMember);
    }
}