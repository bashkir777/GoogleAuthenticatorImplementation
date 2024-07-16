package com.bashkir777.jwtauthservice.data.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "token")
public class RefreshToken {
    @Id
    private Integer id;
    private String token;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
