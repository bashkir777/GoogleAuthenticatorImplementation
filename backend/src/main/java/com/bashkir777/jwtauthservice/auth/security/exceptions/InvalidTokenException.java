package com.bashkir777.jwtauthservice.auth.security.exceptions;

public class InvalidTokenException extends Exception{
    public InvalidTokenException(){
        super("Invalid token");
    }
}
