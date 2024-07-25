package com.bashkir777.jwtauthservice.app.data.repositories;

import com.bashkir777.jwtauthservice.app.data.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Integer> {
    User getUserByUsername(String username);
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :encodedPassword WHERE u.username = :username")
    void resetPassword(String username, String encodedPassword);
}
