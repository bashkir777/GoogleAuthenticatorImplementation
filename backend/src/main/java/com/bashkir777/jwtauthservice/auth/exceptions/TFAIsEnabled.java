package com.bashkir777.jwtauthservice.auth.exceptions;

public class TFAIsEnabled extends Exception{
    public TFAIsEnabled(String message){
        super(message);
    }
}
