package com.mustafaay.library_management_api.repository;

import com.mustafaay.library_management_api.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    // isbn benzersiz olduğu için Optional döndürür
    Optional<Book> findByIsbn(String isbn);

    // aynı isb ile kitap varmı kontrol etmek için kullanılır
    boolean existsByIsbn(String isbn);

    // kitap başlığına göre arama yapar. Büyük/küçük harf duyarsızdır.
    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByCategoryId(Long categoryId);

    List<Book> findByAvailableCopiesGreaterThan(Integer availableCopies);

    @Query("""
        SELECT DISTINCT b FROM Book b
        LEFT JOIN b.authors a
        LEFT JOIN b.category c
        WHERE
            (:q IS NULL OR
             LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%')) OR
             LOWER(a.firstName) LIKE LOWER(CONCAT('%', :q, '%')) OR
             LOWER(a.lastName) LIKE LOWER(CONCAT('%', :q, '%')))
        AND
            (:category IS NULL OR LOWER(c.name) = LOWER(:category))
        AND
            (:status IS NULL OR
             (:status = 'AVAILABLE' AND b.availableCopies > 0) OR
             (:status = 'UNAVAILABLE' AND b.availableCopies = 0))
        """)
    Page<Book> searchBooks(
            @Param("q") String q,
            @Param("category") String category,
            @Param("status") String status,
            Pageable pageable
    );




}