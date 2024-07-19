import React, {useState} from 'react';
import RegisterForm from "./RegisterForm";
import QRCodeForm from "./QRCodeForm";

const RegisterProvider = ({setShowLoginForm}) => {
    const [nextNotClicked, setNextNotClicked] = useState(true);

    return (
        <>
            {nextNotClicked ? <RegisterForm setShowLoginForm={setShowLoginForm} setNextNotClicked={setNextNotClicked}/>
                : <QRCodeForm setNextNotClicked={setNextNotClicked}/>}
        </>
    );
};

export default RegisterProvider;