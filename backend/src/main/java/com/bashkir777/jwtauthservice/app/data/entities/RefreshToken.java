package com.bashkir777.jwtauthservice.app.data.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "token")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue
    private Integer id;

    private String token;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}
