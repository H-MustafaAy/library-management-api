package com.mustafaay.library_management_api.repository;

import com.mustafaay.library_management_api.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

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


}