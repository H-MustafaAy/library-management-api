package com.mustafaay.library_management_api.repository;

import com.mustafaay.library_management_api.entity.Member;
import com.mustafaay.library_management_api.enums.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    //email ile üye bulmak için
    Optional<Member> findByEmail(String email);

    //aynı email ile ikinci kayıtı engellemek için
    boolean existsByEmail(String email);


    //aftif veya pasif üyeleri listelemek için
    List<Member> findByStatus(MemberStatus status);


// ad veya soyada göre arama yapmak için
    List<Member> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName,
            String lastName
    );
}
