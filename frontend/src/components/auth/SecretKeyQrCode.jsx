import React, {useEffect, useRef, useState} from 'react';
import {RegisterFlow} from "../../tools/consts";
import {SECRET_QR_URL} from "../../tools/urls"
import QrCodeComponent from "../../tools/QrCodeComponent";
import {parseOtpAuthUrl} from "../../tools/utils"

const SecretKeyQrCode = ({setCurrentPage, userData, setSecret}) => {

    function goBackToInstallation() {
        setCurrentPage(RegisterFlow.INSTALLATION);
    }
    function goToCodeConfirmation(){
        setCurrentPage(RegisterFlow.CONFIRMATION_CODE);
    }
    const qrComponent = useRef(null);
    const [secretQrUrl, setSecretQrUrl] = useState('');

    useEffect(() => {
        if(secretQrUrl === ''){
            fetch(SECRET_QR_URL + userData.username)
                .then(res => {
                    return res.json();
                })
                .then(data => {
                    setSecretQrUrl(data.secretQrUrl);
                })
                .catch(err => console.log("Error fetching data: ", err));
        }
        if(qrComponent.current && secretQrUrl !== ''){
            qrComponent.current.setUrl(secretQrUrl);
            setSecret(parseOtpAuthUrl(secretQrUrl).secret)
        }
    }, [secretQrUrl]);

    return (
        <>
            <div className="container w-50 p-3 text-center bg-light border border-1 border-dark-subtle rounded-top-5 h2 mb-0 mt-5">
                <div className="row row-cols-5 rounded-3 ms-2">
                    <div className="col col-8 text-center d-flex justify-content-center align-items-center pe-0">
                        Scan this QR code, using Google Authenticator
                    </div>
                    <div className="col col-3 me-3 text-center"><img width="100px" height="100px"
                                                                     src="/images/google-authenticator-logo.svg"
                                                                     className="border bg-light border-1 border-dark-subtle rounded-5"
                                                                     alt="GA logo"/></div>
                </div>
            </div>
            <div className="container bg-light-subtle rounded-bottom-5 border border-1 border-dark-subtle border-top-0 w-50">
                <div className="row">
                     <QrCodeComponent
                        ref={qrComponent}
                        className="col py-4 rounded-4 bg-light"
                        url=""/>
                </div>

            </div>
            <div className="container text-end mt-3 mr-3 w-50">
                <div className="row">
                    <button type="button" onClick={goBackToInstallation}
                            className="btn btn-dark fs-4 rounded-4 py-3 col-5 ">Go back
                    </button>
                    <button type="button" onClick={goToCodeConfirmation}
                            className="btn btn-dark fs-4 rounded-4 py-3 col-5 offset-2">Got it!
                    </button>
                </div>
            </div>
        </>
    );
};

export default SecretKeyQrCode;