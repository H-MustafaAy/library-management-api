package com.mustafaay.library_management_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //ceza hangi üyeye ait
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    //ceza hangi ödünç alma işleminden kaynaklandı
    //her ödünç alma kaydı için en fazla 1 ceza oluşturulabilir.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false, unique = true)
    private Loan loan;

    //ceza miktarı
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    //ceza ödendi mi?
    @Column(nullable = false)
    private boolean paid;

    //ceza açıklaması
    @Column(nullable = false)
    private String reason;

    //ceza oluşturulma tarihi
    @Column(nullable = false)
    private LocalDateTime createdAt;

    //ceza ödeme tarihi
    private LocalDateTime paidAt;

    //kayıt veritabanına eklenmeden  önce otomatik çalışır
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        if (this.paidAt == null && this.paid) {
            this.paidAt = LocalDateTime.now();
        }
    }
}