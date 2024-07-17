package com.bashkir777.jwtauthservice.app.data.exceptions;

public class UserCreationException extends Exception{
    public UserCreationException(){
        super("failed to create a user");
    }
}
