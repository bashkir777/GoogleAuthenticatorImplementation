package com.bashkir777.jwtauthservice.auth.exceptions;

public class TFAIsNotEnabled extends Exception{
    public TFAIsNotEnabled(){
        super("This user has not enabled two factor authentication you should use /login endpoint");
    }
}
