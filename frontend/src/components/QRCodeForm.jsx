import React from 'react';

const QrCodeForm = ({setNextNotClicked}) => {
    const backToRegisterForm = (event) =>{
        event.preventDefault();
        setNextNotClicked(true);
    }
    return (
        <div>
            To do: QR Code Form
            <button onClick={backToRegisterForm}>Back</button>
        </div>
    );
};

export default QrCodeForm;