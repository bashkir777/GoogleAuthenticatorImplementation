package com.bashkir777.jwtauthservice.app.data.repositories;

import com.bashkir777.jwtauthservice.app.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User getUserByUsername(String username);
}
