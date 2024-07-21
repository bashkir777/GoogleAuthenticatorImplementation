import React, {useState} from 'react';
import RegisterProvider from "./RegisterProvider";
import {AuthenticationFlow} from "../../tools/consts";
import LoginProvider from "./LoginProvider";

const AuthenticationProvider = ({setAuthenticated}) => {
    const [authenticationPage, setAuthenticationPage] = useState(AuthenticationFlow.LOGIN)
    return (
        <>
            {authenticationPage === AuthenticationFlow.LOGIN && <LoginProvider setAuthenticationPage={setAuthenticationPage}/>}
            {authenticationPage === AuthenticationFlow.REGISTER &&
                <RegisterProvider setAuthenticated={setAuthenticated} setAuthenticationPage={setAuthenticationPage}/>}
        </>
    );
};

export default AuthenticationProvider;