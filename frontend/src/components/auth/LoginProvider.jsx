import React from 'react';
import {LoginFlow} from "../../tools/consts";
import LoginForm from "./LoginForm";
import ConfirmCodeWindow from "./ConfirmCodeWindow";
import {VERIFICATION_URL} from "../../tools/urls";

const LoginProvider = ({setAuthenticationPage, setAuthenticated}) => {
    const [currentPage, setCurrentPage] = React.useState(LoginFlow.LOGIN);
    const [userData, setUserData] = React.useState({
        username: '',
        password: '',
        otp: ''
    });

    function setUsername(newUsername) {
        setUserData((prevState) => {
            return {
                ...prevState,
                username: newUsername
            }
        })
    }

    function setPassword(newPassword) {
        setUserData((prevState) => {
            return {
                ...prevState,
                password: newPassword
            }
        })
    }

    function setOTP(newOTP) {
        setUserData((prevState) => {
            return {
                ...prevState,
                otp: newOTP
            }
        })
    }

    return (
        <>
            {currentPage === LoginFlow.LOGIN &&
                <LoginForm userData={userData} setAuthenticated={setAuthenticated} setUsername={setUsername} setPassword={setPassword}
                           setAuthenticationPage={setAuthenticationPage} setCurrentPage={setCurrentPage}/>}
            {currentPage === LoginFlow.CONFIRMATION_CODE &&
                <ConfirmCodeWindow setOTP={setOTP} userData={userData} setAuthenticated={setAuthenticated} onSubmitURL={VERIFICATION_URL}
                                   setCurrentPage={setCurrentPage} prevPageFlow={LoginFlow.LOGIN}/>}
        </>
    );
};

export default LoginProvider;