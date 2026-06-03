package com.mustafaay.library_management_api.controller;

import com.mustafaay.library_management_api.dto.request.CreateBookRequest;
import com.mustafaay.library_management_api.dto.request.UpdateBookRequest;
import com.mustafaay.library_management_api.dto.response.BookResponse;
import com.mustafaay.library_management_api.repository.BookRepository;
import com.mustafaay.library_management_api.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final BookRepository bookRepository;

    // kitap ekleme endpointi
    @PostMapping(path = "/create")
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody CreateBookRequest request) {

        BookResponse response = bookService.createBook(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);

        //status → 201 CREATED
        //body   → BookResponse JSON verisi bu çıktıyı elde etmek için bu şekilde yazmak gerekiyor
    }

    @GetMapping(path = "/list")
    public ResponseEntity<List<BookResponse>> getAllBooks() {

        List<BookResponse> books = bookService.getAllBooks();

        return ResponseEntity.ok(books);
    }

    // burada sadece istediğimiz id ye sahip kitabı listeliyoruz.
    @GetMapping(path = "/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id){
        BookResponse response = bookService.getBookById(id);

        return ResponseEntity.ok(response);
    }

    // başlığa göre listeleme
    @GetMapping(path = "/search")
    public ResponseEntity<List<BookResponse>> searchBooksByTitle(@RequestParam String title) {

        List<BookResponse> books = bookService.searchBooksByTitle(title);

        return ResponseEntity.ok(books);
    }

    //kategori id ye göre listeleme
    @GetMapping(path = "/category/{categoryId}")
    public ResponseEntity<List<BookResponse>> getBooksByCategory(@PathVariable Long categoryId) {

        List<BookResponse> books = bookService.getBooksByCategory(categoryId);

        return ResponseEntity.ok(books);
    }

    //güncelle için
    @PutMapping(path = "/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookRequest request) {

        BookResponse response = bookService.updateBook(id, request);

        return ResponseEntity.ok(response);
    }
    //id ye göre silme işlemi.
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        //silme işlemi sonucunda response dönmüyorum

        bookService.deleteBook(id);

        return ResponseEntity.noContent().build();
    }
     // isbn numarası ile listeleniyor
    @GetMapping(path = "/isbn/{isbn}")
    public ResponseEntity<BookResponse> getBookByIsbn(@PathVariable String isbn) {

        BookResponse response = bookService.getBookByIsbn(isbn);

        return ResponseEntity.ok(response);
    }

    //mevcut kitapaları listeler
    @GetMapping(path = "/available")
    public ResponseEntity<List<BookResponse>> getAvailableBooks() {

        List<BookResponse> books = bookService.getAvailableBooks();

        return ResponseEntity.ok(books);
    }




}
