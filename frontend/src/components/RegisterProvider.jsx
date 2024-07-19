import React, {useState} from 'react';
import RegisterForm from "./RegisterForm";
import QRCodeForm from "./QRCodeForm";

const RegisterProvider = ({setShowLoginForm}) => {
    const [nextNotClicked, setNextNotClicked] = useState(true);
    const [userData, changeUserData] = React.useState({
        username: '',
        password: '',
        firstname: '',
        lastname: '',
        secret_key: ''
    });
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
    return (
        <>
            {nextNotClicked ? <RegisterForm changeUsername={changeUsername} changePassword={changePassword}
                                            changeFirstname = {changeFirstname}
                                            changeLastname = {changeLastname}
                                            userData = {userData}
                                            setShowLoginForm={setShowLoginForm}
                                            setNextNotClicked={setNextNotClicked}/>
                : <QRCodeForm setNextNotClicked={setNextNotClicked}/>}
        </>
    );
};

export default RegisterProvider;