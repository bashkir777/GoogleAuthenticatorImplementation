import React, {useState} from 'react';
import RegisterForm from "./RegisterForm";
import GoogleAuthenticatorInstallationPage from "./GoogleAuthenticatorInstallationPage";
import ErrorMessage from "../../tools/ErrorMessage";
import {RegisterFlow} from "../../tools/consts";
import SecretKeyQrCode from "./SecretKeyQrCode";
import ConfirmCodeWindow from "./ConfirmCodeWindow";
import {REGISTER_URL} from "../../tools/urls";

const RegisterProvider = ({setAuthenticationPage, setAuthenticated}) => {
    const [currentPage, setCurrentPage] = useState(RegisterFlow.REGISTER);
    const [error, setError] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [userData, changeUserData] = React.useState({
        username: '',
        password: '',
        firstname: '',
        lastname: '',
        secret: '',
        otp: '',
        tfaEnabled: ''
    });
    const validateString = (inputStr) => {
        if (!inputStr) return false;
        if (inputStr.trim().length < 6) {
            return false;
        }
        const regex = /^[a-zA-Z0-9]+$/;
        return regex.test(inputStr);
    }
    const validateUserData = (userData) => {
        if (!validateString(userData.username)) {
            showError("Username must be at least 6 characters long and contain only Latin letters and digits.")
            return false;
        }
        if (!validateString(userData.password)) {
            showError("Password must be at least 6 characters long and contain only Latin letters and digits.")
            return false;
        }
        if (!validateString(userData.firstname)) {
            showError("Firstname must be at least 6 characters long and contain only Latin letters and digits.")
            return false;
        }
        if (!validateString(userData.lastname)) {
            showError("Lastname must be at least 6 characters long and contain only Latin letters and digits.")
            return false;
        }
        return true;
    }

    const cleanError = () => {
        setError(false);
        setErrorMessage('');
    }
    const showError = (message) => {
        setError(true);
        setErrorMessage(message);
        setTimeout(cleanError, 10000);
    }

    const changeUsername = (event) => {
        changeUserData((prevState) => {
            return {
                ...prevState,
                username: event.target.value,
            }
        });
    }
    const changePassword = (event) => {
        changeUserData((prevState) => {
            return {
                ...prevState,
                password: event.target.value,
            }
        });
    }
    const changeFirstname = (event) => {
        changeUserData((prevState) => {
            return {
                ...prevState,
                firstname: event.target.value,
            }
        });
    }
    const changeLastname = (event) => {
        changeUserData((prevState) => {
            return {
                ...prevState,
                lastname: event.target.value,
            }
        });
    }
    const setSecret = (newSecret) => {
        changeUserData((prevState) => {
            return {
                ...prevState,
                secret: newSecret
            }
        });
    }

    const setOTP = (newOTP) => {
        changeUserData((prevState) => {
            return {
                ...prevState,
                otp: newOTP,
            }
        });
    }

    const setTfaEnabled = (newTfaEnabled) => {
        changeUserData((prevState) => {
            return {
                ...prevState,
                tfaEnabled: newTfaEnabled
            }
        });
    }
    return (
        <>
            {error && <ErrorMessage message={errorMessage} onClose={cleanError}/>}
            {currentPage === RegisterFlow.REGISTER &&
                <RegisterForm changeUsername={changeUsername} changePassword={changePassword}
                              changeFirstname={changeFirstname}
                              changeLastname={changeLastname}
                              validateUserData={validateUserData}
                              setTfaEnabled={setTfaEnabled}
                              setAuthenticated={setAuthenticated}
                              userData={userData}
                              setAuthenticationPage={setAuthenticationPage}
                              setCurrentPage={setCurrentPage}
                              cleanError={cleanError}/>}
            {currentPage === RegisterFlow.INSTALLATION &&
                <GoogleAuthenticatorInstallationPage setCurrentPage={setCurrentPage}/>}

            {currentPage === RegisterFlow.SECRET &&
                <SecretKeyQrCode setSecret={setSecret} userData={userData} setCurrentPage={setCurrentPage}/>}
            {currentPage === RegisterFlow.CONFIRMATION_CODE &&
                <ConfirmCodeWindow onSubmitURL={REGISTER_URL} userData={userData} prevPageFlow={RegisterFlow.SECRET}
                                   setAuthenticated={setAuthenticated} setOTP={setOTP}
                                   setCurrentPage={setCurrentPage}/>}

        </>
    );
};

export default RegisterProvider;