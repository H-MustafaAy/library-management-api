package com.mustafaay.library_management_api.service;

import com.mustafaay.library_management_api.dto.request.LoginRequest;
import com.mustafaay.library_management_api.dto.response.LoginResponse;
import com.mustafaay.library_management_api.entity.Member;
import com.mustafaay.library_management_api.exception.BadRequestException;
import com.mustafaay.library_management_api.repository.MemberRepository;
import com.mustafaay.library_management_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {

        //requestten gelen email kontrolü
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Email veya şifre hatalı"));

        //requestten gelen password ile kullanıcının passwordu eşleşiyor mu
        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                member.getPassword()
        );
        // şifre doğru değilse dönülecek hata
        if (!passwordMatches) {
            throw new BadRequestException("Email veya şifre hatalı");
        }
        //email ve şifre doğruysa token oluşturulur
        String token = jwtService.generateToken(member);

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .memberId(member.getId())
                .email(member.getEmail())
                .fullName(member.getFirstName() + " " + member.getLastName())
                .role(member.getRole())
                .build();
    }
}