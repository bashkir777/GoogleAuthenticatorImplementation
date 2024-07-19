import React, {useState} from 'react';
import LoginForm from "./LoginForm";
import RegisterProvider from "./RegisterProvider";

const AuthenticationProvider = () => {
    const [showLoginForm, setShowLoginForm] = useState(true);
    return (
        <>
            {showLoginForm ? <LoginForm setShowLoginForm = {setShowLoginForm}/>
                : <RegisterProvider setShowLoginForm = {setShowLoginForm}/>}
        </>
    );
};

export default AuthenticationProvider;