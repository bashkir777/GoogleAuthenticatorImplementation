package com.bashkir777.jwtauthservice.auth.exceptions;

public class InvalidCode extends Exception{
    public InvalidCode(){
        super("Invalid 2FA code");
    }
}
