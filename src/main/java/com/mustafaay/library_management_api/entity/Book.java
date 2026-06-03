package com.mustafaay.library_management_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //kitap için benzersiz kimlikk
    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private String title;

    //yayın yılı
    @Column(name = "publication_year")
    private Integer publicationYear;

    // bu kitaptan toplam kaç adet var?
    @Column(name = "total_copies", nullable = false)
    private Integer totalCopies;

    //  rafta verilebilir kaç adet var?
    @Column(name = "available_copies", nullable = false)
    private Integer availableCopies;

    @ManyToOne(fetch = FetchType.LAZY)
    //lazy kullanmak daha mantıklı gerçekten lazım olan veri çekilir
    //gereksiz yere çok fazla sorguya gerek yok,her kitap çekildiğinde kategori detayının otomatik gelmesini istemiyorum
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    @Builder.Default
    private Set<Author> authors = new HashSet<>();
    //burada set kullandım , tekrar eden yazar isimlerini engellemem gerekiyor aynı kitaba iki kere aynı yazar yazılmamalı


}
