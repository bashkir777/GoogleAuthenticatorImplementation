import React from 'react';
import '../../styles/utility-styles.css'
import QrCodeComponent from "../../tools/QrCodeComponent";
import {RegisterFlow} from "../../tools/consts";

const GoogleAuthenticatorInstallationPage = ({setCurrentPage}) => {
    const backToRegisterForm = () => {
        setCurrentPage(RegisterFlow.REGISTER);
    }
    const goToSecretKeyScan = () => {
        setCurrentPage(RegisterFlow.SECRET);
    }

    return (
        <>
            <div className="container p-5 py-3 my-3 text-center bg-light-subtle rounded-5 h2">
                <div className="row ms-2">
                    <div className="col-10 text-center d-flex justify-content-center align-items-center pe-0">Please, Download Google Authenticator
                        Mobile App via QR Code
                    </div>
                    <div className="col-2 text-center" ><img width="100px" height="100px" src="/images/google-authenticator-logo.svg"
                                                   className="border bg-light border-1 border-dark-subtle rounded-5"
                                                   alt="GA logo"/></div>
                </div>
            </div>
            <div className="container">
                <div className="row">
                    <div className="col-md-6 text-center bg-light py-3 rounded-start-5">
                        <h2>Download from the App Store</h2>
                        <QrCodeComponent className={"border border-2 border-dark-subtle rounded-5"} url={"https://apps.apple.com/us/app/google-authenticator/id388497605"}/>
                        <p className="my-1">Use this QR code to download Google Authenticator from the App Store. Open the App Store on
                            your iPhone and scan the code to install the app.</p>
                    </div>
                    <div className="col-md-6 text-center text-light bg-black pt-3 pb-2 rounded-end-5">
                        <h2>Download from Google Play</h2>
                        <QrCodeComponent className={"rounded-5"} url={"https://play.google.com/store/search?q=google+authenticator&c=apps"}/>
                        <p className="my-1">Use this QR code to download Google Authenticator from Google Play. Open the Google Play
                            Store on your Android device and scan the code to install the app.</p>
                    </div>
                </div>
            </div>
            <div className="container text-end mt-3 mr-3">
                <div className="row">
                    <button type="button" onClick={backToRegisterForm} className="btn btn-dark fs-4 rounded-4 py-3 col-3 offset-1">Go back</button>
                    <button type="button" onClick={goToSecretKeyScan} className="btn btn-dark fs-4 rounded-4 py-3 col-3 offset-4">Got it!</button>
                </div>
            </div>
        </>
    );
};

export default GoogleAuthenticatorInstallationPage;