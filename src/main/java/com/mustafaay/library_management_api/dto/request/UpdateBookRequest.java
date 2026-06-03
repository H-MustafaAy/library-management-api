package com.mustafaay.library_management_api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UpdateBookRequest {

    // isbn numarası boş olamaz
    @NotBlank(message = "ISBN boş olamaz")
    private String isbn;

    // başlık boş olamaz
    @NotBlank(message = "Kitap başlığı boş olamaz")
    private String title;

    // y.yılı boş olamaz
    @NotNull(message = "Yayın yılı boş olamaz")
    private Integer publicationYear;

    // toplam kopya sayısı en az 1 olmalı
    @NotNull(message = "Toplam kopya sayısı boş olamaz")
    @Min(value = 1, message = "Toplam kopya sayısı en az 1 olmalıdır")
    private Integer totalCopies;

    // mevcut kopya sayısı negatif olamaz
    @NotNull(message = "Mevcut kopya sayısı boş olamaz")
    @Min(value = 0, message = "Mevcut kopya sayısı negatif olamaz")
    private Integer availableCopies;

    // kitabın kategorisi
    @NotNull(message = "Kategori ID boş olamaz")
    private Long categoryId;

    // kitabın yazarları
    @NotNull(message = "Yazar ID listesi boş olamaz")
    private Set<Long> authorIds;
}
