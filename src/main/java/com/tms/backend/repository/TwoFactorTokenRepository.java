package com.tms.backend.repository;

import com.tms.backend.model.TwoFactorToken;
import com.tms.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import jakarta.transaction.Transactional;

@Repository
public interface TwoFactorTokenRepository extends JpaRepository<TwoFactorToken, Long> {
    Optional<TwoFactorToken> findByUserAndVerifiedFalse(User user);
    @Transactional
    void deleteByUser(User user);
}
