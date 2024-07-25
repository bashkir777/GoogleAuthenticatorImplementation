import React, {useState} from 'react';
import {ChangePasswordFlow} from "../../tools/consts";
import ChangePasswordEnterUsernameForm from "./ChangePasswordEnterUsernameForm";
import ChangePasswordForm from "./ChangePasswordForm";

const ChangePasswordProvider = ({setCurrentPage, setUserData, setSubmitURL, userData, setPassword, setUsername}) => {
    const [currentPasswordChangePage, setCurrentPasswordChangePage]
        = useState(ChangePasswordFlow.ENTER_USERNAME);

    return (
        <>
            {currentPasswordChangePage === ChangePasswordFlow.ENTER_USERNAME &&
                <ChangePasswordEnterUsernameForm setCurrentPage={setCurrentPage}
                                                 setCurrentPasswordChangePage={setCurrentPasswordChangePage}
                                                 userData={userData} setUsername={setUsername}/>}
            {currentPasswordChangePage === ChangePasswordFlow.CHANGE_PASSWORD &&
                <ChangePasswordForm setUserData={setUserData} setSubmitURL={setSubmitURL} userData={userData} setCurrentPage={setCurrentPage} setPassword={setPassword}/>}
        </>
    );
};

export default ChangePasswordProvider;