package com.example.auth.repository;

import com.example.auth.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM Otp o WHERE o.email = :email")
    void deleteByEmail(String email);
}

