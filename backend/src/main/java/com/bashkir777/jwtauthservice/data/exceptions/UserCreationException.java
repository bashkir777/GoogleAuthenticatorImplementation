package com.bashkir777.jwtauthservice.data.exceptions;

public class UserCreationException extends Exception{
    public UserCreationException(){
        super("failed to create a user");
    }
}
