# Library Management API

Spring Boot ile geliştirilmiş RESTful Kütüphane Yönetim Sistemi backend projesidir.

Bu proje; kategori, yazar, kitap, üye ve ödünç alma/iade işlemlerini yönetmek amacıyla geliştirilmiştir. Projede katmanlı mimari kullanılmış, DTO yapısı uygulanmış, merkezi hata yönetimi eklenmiş ve geliştirme ortamı için H2 veritabanı yapılandırılmıştır.

---

## Kullanılan Teknolojiler

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Spring Security
- H2 Database
- Bean Validation
- Lombok
- Maven

---

## Proje Yapısı

```text
src/main/java/com/mustafaay/library_management_api
│
├── config
├── controller
├── dto
│   ├── request
│   └── response
├── entity
├── enums
├── exception
├── mapper
├── repository
└── service
```

---

## Modüller

Projede şu modüller bulunmaktadır:

- Category
- Author
- Book
- Member
- Loan

---

## Mevcut Proje Durumu

Şu ana kadar tamamlanan ana bölümler:

- Katmanlı proje mimarisi
- H2 veritabanı bağlantısı
- SecurityConfig yapılandırması
- Book CRUD işlemleri
- Member CRUD işlemleri
- Loan ödünç alma/iade işlemleri
- DTO request/response yapıları
- Mapper yapıları
- Repository ve Service katmanları
- Merkezi hata yönetimi
- Validation yapısı
- README dokümantasyonu
- - Fine/Ceza modülü
- - Faz 2 iş kurallarının tamamlanması

Henüz geliştirilmesi planlanan bölümler:



- Pagination ve sorting desteği
- Swagger/OpenAPI dokümantasyonu
- Unit test ve integration testler
- PostgreSQL profili
- JWT authentication
- Role bazlı authorization

---

## Veritabanı

Projede geliştirme ortamı için H2 Database kullanılmaktadır.

### H2 Console Bilgileri

```text
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:file:./data/librarydb
Username: sa
Password: boş
```

---

## application.properties

```properties
spring.application.name=library-management-api

spring.datasource.url=jdbc:h2:file:./data/librarydb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

server.port=8080
```

---

# API Endpointleri

## Book Endpointleri

| Method | Endpoint | Açıklama |
|---|---|---|
| POST | `/api/books/create` | Yeni kitap oluşturur |
| GET | `/api/books/list` | Tüm kitapları listeler |
| GET | `/api/books/{id}` | ID’ye göre kitap getirir |
| GET | `/api/books/search?title=...` | Başlığa göre kitap arar |
| GET | `/api/books/category/{categoryId}` | Kategoriye göre kitapları listeler |
| GET | `/api/books/isbn/{isbn}` | ISBN numarasına göre kitap getirir |
| GET | `/api/books/available` | Rafta bulunan kitapları listeler |
| PUT | `/api/books/{id}` | Kitap bilgilerini günceller |
| DELETE | `/api/books/{id}` | Kitap siler |

### Örnek Book Request

```json
{
  "isbn": "9789753638029",
  "title": "Kürk Mantolu Madonna",
  "publicationYear": 1943,
  "totalCopies": 5,
  "availableCopies": 5,
  "categoryId": 1,
  "authorIds": [1]
}
```

---

## Member Endpointleri

| Method | Endpoint | Açıklama |
|---|---|---|
| POST | `/api/members/create` | Yeni üye oluşturur |
| GET | `/api/members/list` | Tüm üyeleri listeler |
| GET | `/api/members/{id}` | ID’ye göre üye getirir |
| GET | `/api/members/email/{email}` | Email adresine göre üye getirir |
| GET | `/api/members/search?keyword=...` | Ad veya soyada göre üye arar |
| GET | `/api/members/active` | Aktif üyeleri listeler |
| GET | `/api/members/passive` | Pasif üyeleri listeler |
| PUT | `/api/members/{id}` | Üye bilgilerini günceller |
| PATCH | `/api/members/{id}/deactivate` | Üyeyi pasif hale getirir |
| DELETE | `/api/members/{id}` | Üye siler |

### Örnek Member Request

```json
{
  "firstName": "Ahmet",
  "lastName": "Yılmaz",
  "email": "ahmet@example.com",
  "phoneNumber": "5551234567",
  "address": "Konya"
}
```

Yeni üye oluşturulurken varsayılan olarak:

```text
status = ACTIVE
role = MEMBER
```

atanır.

---

## Loan Endpointleri

| Method | Endpoint | Açıklama |
|---|---|---|
| POST | `/api/loans/create` | Kitap ödünç verir |
| GET | `/api/loans/list` | Tüm ödünç kayıtlarını listeler |
| GET | `/api/loans/{id}` | ID’ye göre ödünç kaydı getirir |
| GET | `/api/loans/member/{memberId}` | Üyeye göre ödünç kayıtlarını listeler |
| GET | `/api/loans/book/{bookId}` | Kitaba göre ödünç kayıtlarını listeler |
| GET | `/api/loans/status/{status}` | Duruma göre ödünç kayıtlarını listeler |
| GET | `/api/loans/borrowed` | İade edilmemiş ödünç kayıtlarını listeler |
| GET | `/api/loans/returned` | İade edilmiş ödünç kayıtlarını listeler |
| GET | `/api/loans/overdue` | Süresi geçmiş ödünç kayıtlarını listeler |
| PATCH | `/api/loans/{id}/return` | Kitap iade işlemi yapar |
| DELETE | `/api/loans/{id}` | İade edilmiş ödünç kaydını siler |

### Örnek Loan Request

```json
{
  "bookId": 1,
  "memberId": 1
}
```

Kitap ödünç verildiğinde sistem otomatik olarak:

```text
loanDate = bugünün tarihi
dueDate = bugünden 14 gün sonrası
status = BORROWED
returnDate = null
```

değerlerini oluşturur.

Kitap iade edilirken kullanılan ID, kitap ID’si değil ödünç kaydı ID’sidir.

```text
PATCH /api/loans/{loanId}/return
```

---

## İş Kuralları

Şu anda uygulanan iş kuralları:

- Pasif üyeler kitap ödünç alamaz.
- Mevcut kopyası 0 olan kitap ödünç verilemez.
- Kitap ödünç verilince `availableCopies` değeri 1 azalır.
- Kitap iade edilince `availableCopies` değeri 1 artar.
- Ödünç süresi otomatik olarak 14 gün belirlenir.
- Zaten iade edilmiş ödünç kaydı tekrar iade edilemez.
- Sadece iade edilmiş ödünç kayıtları silinebilir.
- Süresi geçmiş ödünç kayıtları listelenebilir.

Geliştirilmesi planlanan iş kuralları:

- Bir üye aynı anda en fazla 3 kitap ödünç alabilir.
- Üye elinde bulunan aynı kitabı tekrar alamaz.
- Elinde iade edilmemiş kitabı olan üye silinemez.
- Ödünçte kopyası olan kitap silinemez.
- Geciken iadeler için gün başına ceza oluşturulur.
- Ödenmemiş cezası olan üye yeni kitap alamaz.

---

## Enumlar

### MemberStatus

```text
ACTIVE,
PASSIVE
```
```text
### LoanStatus

```java
BORROWED,
RETURNED,
OVERDUE
```

### Role

```text
ADMIN,
LIBRARIAN,
MEMBER
```

---

## Exception Handling

Projede merkezi hata yönetimi için `GlobalExceptionHandler` kullanılmıştır.

Exception yapısı:

```text
exception
├── ResourceNotFoundException
├── BadRequestException
├── ErrorResponse
└── GlobalExceptionHandler
```

### Hata Response Formatı

```json
{
  "timestamp": "2026-06-03T16:04:45.1774603",
  "status": 404,
  "error": "Not Found",
  "message": "Üye bulunamadı. ID: 99",
  "path": "/api/members/99"
}
```

### Yakalanan Hata Türleri

| Exception | HTTP Status | Açıklama |
|---|---|---|
| ResourceNotFoundException | 404 | Kayıt bulunamadığında |
| BadRequestException | 400 | Hatalı işlem yapılmak istendiğinde |
| MethodArgumentNotValidException | 400 | DTO validation hatalarında |
| MethodArgumentTypeMismatchException | 400 | Yanlış enum veya parametre tipinde |
| Exception | 500 | Beklenmeyen hatalarda |

---

## Security

Projede Spring Security dependency’si bulunmaktadır. Ancak geliştirme aşamasında tüm endpointler serbest bırakılmıştır.

H2 Console erişimi için frame options kapatılmıştır.
```text
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
```

---

## Temel İş Akışı

1. Kategori oluşturulur.
2. Yazar oluşturulur.
3. Kitap oluşturulur.
4. Üye oluşturulur.
5. Üye kitap ödünç alır.
6. Kitabın `availableCopies` değeri 1 azalır.
7. Kitap iade edildiğinde `availableCopies` değeri 1 artar.
8. Ödünç kaydı `RETURNED` durumuna alınır.

---

## Projeyi Çalıştırma

Projeyi çalıştırmak için:

```bash
mvn spring-boot:run
```

veya IDE üzerinden ana application sınıfı çalıştırılabilir.

API varsayılan olarak şu adreste çalışır:

```text
http://localhost:8080
```

---

## Git Commit Notu

Bu proje geliştirilirken yapılan değişikliklerin faz faz ve anlamlı commit mesajlarıyla GitHub’a aktarılması hedeflenmektedir.

Örnek commit mesajları:

```text
Temel CRUD, ödünç işlemleri ve hata yönetimini ekle
README dokümantasyonunu güncelle
Loan iş kurallarını geliştir
Swagger dokümantasyonunu ekle
```

---

## Yapılanlar

Projenin mevcut aşamasında kütüphane yönetim sisteminin temel backend yapısı büyük ölçüde tamamlanmıştır.

Tamamlanan başlıca geliştirmeler:

- Spring Boot proje yapısı oluşturuldu.
- Katmanlı mimari yapısı kuruldu.
    - Controller
    - Service
    - Repository
    - Entity
    - DTO
- Book modülü geliştirildi.
    - Kitap ekleme
    - Kitap listeleme
    - Kitap güncelleme
    - Kitap silme
- Member modülü geliştirildi.
    - Üye ekleme
    - Üye listeleme
    - Üye güncelleme
    - Üye silme
- Loan modülü geliştirildi.
    - Kitap ödünç verme
    - Kitap iade alma
    - Aktif ödünç kayıtlarını yönetme
- Exception handling yapısı eklendi.
    - Not found hataları
    - Bad request hataları
    - Global hata yönetimi
- H2 veritabanı ve H2 Console yapılandırıldı.
- SecurityConfig ile geliştirme ortamına uygun temel güvenlik ayarları yapıldı.
- README dosyası düzenlendi.
- Loan iş kuralları eklendi.
    - Bir üye aynı anda en fazla 3 kitap ödünç alabilir.
    - Üye elinde bulunan aynı kitabı tekrar ödünç alamaz.
    - Ödenmemiş cezası olan üye yeni kitap ödünç alamaz.
- Member silme kuralı eklendi.
    - İade edilmemiş kitabı bulunan üye silinemez.
- Book silme kuralı eklendi.
    - Ödünçte kopyası bulunan kitap silinemez.