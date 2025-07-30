package com.firomsa.ecommerce.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.firomsa.ecommerce.model.ConfirmationToken;
import com.firomsa.ecommerce.model.User;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    Optional<ConfirmationToken> findByToken(String token);

    void deleteAllByUserAndConfirmedAtIsNull(User user);

    Optional<ConfirmationToken> findByUserAndExpiresAtAfter(User user, LocalDateTime date);
}
