package com.mustafaay.library_management_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMemberRequest {

    @NotBlank(message = "Ad boş bırakılamaz!")
    private String firstName;

    @NotBlank(message = "Soyad boş bırakılamaz!")
    private String lastName;


    @NotBlank(message = "Email boş bırakılamaz")
    @Email(message = "Geçerli bir email adresi giriniz")
    private String email;

    @NotBlank(message = "Telefon numarası boş bırakılamaz")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Telefon numarası başında sıfır olmadan 10 rakam olmalıdır"
    )
    private String phoneNumber;

    @Size(max = 500, message = "Adres en fazla 500 karakter olabilir")
    private String address;
}
