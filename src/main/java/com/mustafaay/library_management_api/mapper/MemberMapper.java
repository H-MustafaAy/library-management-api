package com.mustafaay.library_management_api.mapper;

import com.mustafaay.library_management_api.dto.request.CreateMemberRequest;
import com.mustafaay.library_management_api.dto.request.UpdateMemberRequest;
import com.mustafaay.library_management_api.dto.response.MemberResponse;
import com.mustafaay.library_management_api.entity.Member;
import com.mustafaay.library_management_api.enums.MemberStatus;
import com.mustafaay.library_management_api.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    // yeni üye oluştururken requesti member türüne dönüştürür
    public Member toEntity(CreateMemberRequest request) {
        return Member.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .status(MemberStatus.ACTIVE)
                .role(Role.MEMBER)
                .build();
    }

    // var olan üyeyi güncellenemek için requesti member türüne dönüştürüyor
    public void updateEntity(Member member, UpdateMemberRequest request) {
        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setEmail(request.getEmail());
        member.setPhoneNumber(request.getPhoneNumber());
        member.setAddress(request.getAddress());

        if (request.getStatus() != null) {
            member.setStatus(request.getStatus());
        }

        if (request.getRole() != null) {
            member.setRole(request.getRole());
        }
    }
    // bilgi güvenliği için sadece response türünden veri dönmeliyiz , bu yüzden member türünü
    // response türüne çeviriyoruz
    public MemberResponse toResponse(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .fullName(member.getFirstName() + " " + member.getLastName())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .status(member.getStatus())
                .role(member.getRole())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}