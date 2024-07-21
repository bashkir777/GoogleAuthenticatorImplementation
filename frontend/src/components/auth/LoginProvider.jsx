import React from 'react';
import {LoginFlow} from "../../tools/consts";
import LoginForm from "./LoginForm";
import ConfirmCodeWindow from "./ConfirmCodeWindow";

const LoginProvider = ({setAuthenticationPage}) => {
    const[currentPage, setCurrentPage] = React.useState(LoginFlow.LOGIN);
    return (
        <>
            {currentPage === LoginFlow.LOGIN && <LoginForm setAuthenticationPage={setAuthenticationPage}/>}
            {currentPage === LoginFlow.CONFIRMATION_CODE && <ConfirmCodeWindow setCurrentPage={setCurrentPage} prevPageFlow={LoginFlow.LOGIN}/>}
        </>
    );
};

export default LoginProvider;