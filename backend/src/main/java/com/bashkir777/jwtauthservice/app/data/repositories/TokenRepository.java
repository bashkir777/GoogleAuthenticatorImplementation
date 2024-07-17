package com.bashkir777.jwtauthservice.app.data.repositories;

import com.bashkir777.jwtauthservice.app.data.entities.RefreshToken;
import com.bashkir777.jwtauthservice.app.data.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findRefreshTokenByUser(User user);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM RefreshToken rt WHERE rt.user = ?1")
    void deleteRefreshTokenByUser(User user);
}
