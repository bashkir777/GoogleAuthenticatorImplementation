import React, {useState} from 'react';
import LoginForm from "./LoginForm";
import RegisterProvider from "./RegisterProvider";

const AuthenticationProvider = ({setAuthenticated}) => {
    const [showLoginForm, setShowLoginForm] = useState(true);
    return (
        <>
            {showLoginForm ? <LoginForm setShowLoginForm = {setShowLoginForm}/>
                : <RegisterProvider setAuthenticated={setAuthenticated} setShowLoginForm = {setShowLoginForm}/>}
        </>
    );
};

export default AuthenticationProvider;