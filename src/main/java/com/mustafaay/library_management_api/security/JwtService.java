package com.mustafaay.library_management_api.security;

import com.mustafaay.library_management_api.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
//token üretir
    public String generateToken(Member member) {
        return Jwts.builder()
                //tokenin ana kimliği email oluyor
                .subject(member.getEmail())
                //rol bilgisi koyuluyor
                .claim("role", member.getRole().name())
                //member ıd koyuluyor
                .claim("memberId", member.getId())
                //tokenin ne zaman üretildiği yazılır
                .issuedAt(new Date(System.currentTimeMillis()))
                //token ne zaman bitecek
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                // tokeni secret key ile imzalanır . sonradan değiştirilirse geçersiz sayılır
                .signWith(getSignInKey())
                .compact();
    }

    public String extractEmail(String token) {
        //yukarıda tokeni oluştururken emaili subject alanına koymuştum bu metot email döndürür.
        return extractAllClaims(token).getSubject();
    }
    //token süresi dolmuş mu kontrolü yapar
    //tokenin expried süresi şuandan önceyse false döner
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    public boolean isTokenValid(String token, Member member) {
        String email = extractEmail(token);
        //tokenin içindeki email ile veritabanındaki member email aynı mı kontrolü yapılır ve token süresi dolmuş mu
        return email.equals(member.getEmail()) && !isTokenExpired(token);
    }

//tokeni doğrulayıp içindeki bilgileri çıkarır
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                //tokenin imzası kontrol edilir
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // secret key oluşturma, base64 olarak decode ediliyor ve jwt imzalama anahtarına çeviriliyor
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}