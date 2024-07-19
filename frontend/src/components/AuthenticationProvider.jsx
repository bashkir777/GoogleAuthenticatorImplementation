import React, {useState} from 'react';
import LoginForm from "./LoginForm";
import RegisterForm from "./RegisterForm";

const AuthenticationProvider = () => {
    const [showLoginForm, setShowLoginForm] = useState(true);
    return (
        <>
            {showLoginForm ? <LoginForm setShowLoginForm = {setShowLoginForm}/>
                : <RegisterForm setShowLoginForm = {setShowLoginForm}/>}
        </>
    );
};

export default AuthenticationProvider;