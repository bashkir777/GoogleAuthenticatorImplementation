import React, {useState} from 'react';
import {LoginFlow} from "../../tools/consts";
import LoginForm from "./LoginForm";
import ConfirmCodeWindow from "./ConfirmCodeWindow";
import {LOGIN_URL} from "../../tools/urls";
import ChangePasswordForm from "./ChangePasswordForm";
import ChangePasswordProvider from "./ChangePasswordProvider";

const LoginProvider = ({setAuthenticationPage, setAuthenticated}) => {
    const [currentPage, setCurrentPage] = useState(LoginFlow.LOGIN);
    const [onSubmitURL, setSubmitURL] = useState(LOGIN_URL);
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
                <LoginForm userData={userData} setAuthenticated={setAuthenticated} setUsername={setUsername}
                           setPassword={setPassword}
                           setAuthenticationPage={setAuthenticationPage} setCurrentPage={setCurrentPage}/>}
            {currentPage === LoginFlow.CHANGE_PASSWORD &&
                <ChangePasswordProvider setUserData={setUserData} userData={userData} setCurrentPage={setCurrentPage}
                                        setUsername={setUsername} setPassword={setPassword} setSubmitURL={setSubmitURL}/>}
            {currentPage === LoginFlow.CONFIRMATION_CODE &&
                <ConfirmCodeWindow setOTP={setOTP} userData={userData} setAuthenticated={setAuthenticated}
                                   onSubmitURL={onSubmitURL}
                                   setCurrentPage={setCurrentPage} prevPageFlow={LoginFlow.LOGIN}/>}
        </>
    );
};

export default LoginProvider;