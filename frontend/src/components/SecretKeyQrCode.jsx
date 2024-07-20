import React from 'react';
import {RegisterFlow} from "../tools/enums";

const SecretKeyQrCode = ({setCurrentPage}) => {
    function goBackToInstallation(){
        setCurrentPage(RegisterFlow.INSTALLATION);
    }
    return (
        <button onClick={goBackToInstallation}>
            Back
        </button>
    );
};

export default SecretKeyQrCode;