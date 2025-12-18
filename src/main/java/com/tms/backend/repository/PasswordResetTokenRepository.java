package com.tms.backend.repository;

import com.tms.backend.model.PasswordResetToken;
import com.tms.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import jakarta.transaction.Transactional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    @Transactional
    void deleteByUser(User user);
}
