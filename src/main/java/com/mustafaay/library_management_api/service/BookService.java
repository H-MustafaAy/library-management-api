package com.mustafaay.library_management_api.service;

import com.mustafaay.library_management_api.dto.request.CreateBookRequest;
import com.mustafaay.library_management_api.dto.request.UpdateBookRequest;
import com.mustafaay.library_management_api.dto.response.BookResponse;
import com.mustafaay.library_management_api.entity.Author;
import com.mustafaay.library_management_api.entity.Book;
import com.mustafaay.library_management_api.entity.Category;
import com.mustafaay.library_management_api.repository.AuthorRepository;
import com.mustafaay.library_management_api.repository.BookRepository;
import com.mustafaay.library_management_api.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mustafaay.library_management_api.exception.BadRequestException;
import com.mustafaay.library_management_api.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository,
                       CategoryRepository categoryRepository,
                       AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
    }

    @Transactional
    public BookResponse createBook(CreateBookRequest request) {

        //aynı isbn ye sahip iki kitap olamaz primery key
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BadRequestException("Bu isbn numarasına sahip kitap mevcut!");
        }
        //mevcut kopya sayısı toplam kopya sayısından fazla olamaz
        if (request.getAvailableCopies() > request.getTotalCopies()) {
            throw new BadRequestException("Mevcut kopya sayısı toplam kopya sayısından fazla olamaz!");
        }

        //gelen category id ile kategori bulunur.
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı"));


        // gelen authorIds listesindeki  ıd lere  göre yazarlar  bulunur.
        Set<Author> authors = request.getAuthorIds()
                .stream()
                .map(authorId -> authorRepository.findById(authorId)
                        .orElseThrow(() -> new ResourceNotFoundException("Yazar bulunamadı. Id :" + authorId)))
                .collect(Collectors.toSet());

        Book book = Book.builder()
                .isbn(request.getIsbn())
                .title(request.getTitle())
                .publicationYear(request.getPublicationYear())
                .totalCopies(request.getTotalCopies())
                .availableCopies(request.getAvailableCopies())
                .category(category)
                .authors(authors)
                .build();

        // yukarıda oluşturduğum book nesnesini veritabanına savedBook olarak kaydettim.
        Book savedBook = bookRepository.save(book);

        return mapToBookResponse(savedBook);
    }

    //tüm kitapları getirmek için
    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks() {

        return bookRepository.findAll()
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    // sadece istediğimiz idye sahip kitabı listelemek için
    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id){
        Book book = bookRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Kitap bulunamadı! Id :" +id));

        // yine burada BookResponse türüne dönüştürmek için aşağıda yazdığım metodu kullandım.
        return mapToBookResponse(book);

    }

    // başlığa göre listeleme
    @Transactional(readOnly = true)
    public List<BookResponse> searchBooksByTitle(String title) {

        return bookRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getBooksByCategory(Long categoryId) {

        return bookRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    // kitap güncellemek için.
    @Transactional
    public BookResponse updateBook(Long id , UpdateBookRequest request){
        //parametreden gelen id numarasına sahip kitap var mı kontrol ediliyor
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kitap bulunamadı. ID: " + id));

        // Eğer isbn değiştiriliyorsa yeni isbn başka bir kitaba ait mi kontrol ediliyor
        if (!book.getIsbn().equals(request.getIsbn()) &&
                bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BadRequestException("Bu ISBN numarasına sahip başka bir kitap zaten mevcut.");
        }

        // mevcut kopya sayısı toplam kopyadan fazla olamaz
        if (request.getAvailableCopies() > request.getTotalCopies()) {
            throw new BadRequestException("Mevcut kopya sayısı toplam kopya sayısından fazla olamaz.");
        }
        // kategorisi var mı
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı. ID: " + request.getCategoryId()));
        // yazarlar  var mı
        Set<Author> authors = request.getAuthorIds()
                .stream()
                .map(authorId -> authorRepository.findById(authorId)
                        .orElseThrow(() -> new ResourceNotFoundException("Yazar bulunamadı. ID: " + authorId)))
                .collect(Collectors.toSet());

        // mevcut kitap nesnesini güncelliyor
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setPublicationYear(request.getPublicationYear());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(request.getAvailableCopies());
        book.setCategory(category);
        book.setAuthors(authors);

        Book updatedBook = bookRepository.save(book);

        return mapToBookResponse(updatedBook);
    }

    @Transactional
    public void deleteBook(Long id) {

        // silinecek kitap var mı kontrol ediyoruz
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kitap bulunamadı. ID: " + id));

        // sitabı siliyoruz
        bookRepository.delete(book);
    }
   // isbn numarası ile listleme için
    @Transactional(readOnly = true)
    public BookResponse getBookByIsbn(String isbn) {

        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Kitap bulunamadı. ISBN: " + isbn));

        return mapToBookResponse(book);
    }

    // RAFTA BULUNAN KİTAPLARI LİSTELEMEK İÇİN
    @Transactional(readOnly = true)
    public List<BookResponse> getAvailableBooks() {

        return bookRepository.findByAvailableCopiesGreaterThan(0)
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }



    // bu metodu kullanıcı response için kullanıyoruz bilgi güvenliği için
    private BookResponse mapToBookResponse(Book book) {

        Set<String> authorNames = book.getAuthors()
                .stream()
                .map(author -> author.getFirstName() + " " + author.getLastName())
                .collect(Collectors.toSet());

        return BookResponse.builder()
                .id(book.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .publicationYear(book.getPublicationYear())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .categoryId(book.getCategory().getId())
                .categoryName(book.getCategory().getName())
                .authors(authorNames)
                .build();
    }
}
