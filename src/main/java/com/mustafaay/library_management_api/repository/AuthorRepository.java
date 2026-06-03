package com.mustafaay.library_management_api.repository;

import com.mustafaay.library_management_api.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    // Yazarın adına göre arama yapar
    List<Author> findByFirstNameContainingIgnoreCase(String firstName);

    // Yazar soyadına göre arama yapar
    List<Author> findByLastNameContainingIgnoreCase(String lastName);
}