package com.bashkir777.jwtauthservice.app.data.exceptions;

public class NoSuchUserException extends Exception{
    public NoSuchUserException(){
        super("There is no user with this username");
    }
}
