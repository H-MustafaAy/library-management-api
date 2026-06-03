package com.mustafaay.library_management_api.dto.response;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {
    // bu sınıfı kullanıcıya kitap bilgisi dönerken kullanacağım.

    private Long id;

    private String isbn;

    private String title;

    private Integer publicationYear;

    private Integer totalCopies;

    private Integer availableCopies;

    private Long categoryId;

    private String categoryName;

    private Set<String> authors;
}
