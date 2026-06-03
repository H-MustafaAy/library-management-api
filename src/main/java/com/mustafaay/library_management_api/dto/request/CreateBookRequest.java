package com.mustafaay.library_management_api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CreateBookRequest {

    @NotBlank(message = "ISBN boş olamaz")
    private String isbn;
    @NotBlank(message = "Kitap başlığı boş olamaz")
    private String title;
    @NotNull(message = "Yayın yılı boş olamaz")
    private Integer publicationYear;
    @NotNull(message = "Toplam kopya sayısı boş olamaz ")
    @Min(value = 1, message = "Toplam kopya sayısı en az 1 olmalıdır")
    private Integer totalCopies;
    @NotNull(message = "mevcut kopya sayısı boş olamaz")
    @Min(value = 0,message = "mevcut kopya sayısı negatif olamaz")
    private Integer availableCopies;
    @NotNull(message = "yazar ıd listesi boş olamaz")
    private Long categoryId;
    @NotNull(message = "Yazar ID listesi boş olamaz")
    private Set<Long> authorIds;
}
