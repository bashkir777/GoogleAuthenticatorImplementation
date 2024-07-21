import React, {useState, useRef, useEffect} from 'react';
import {RegisterFlow} from "../../tools/consts";
import ErrorMessage from "../../tools/ErrorMessage";
import {REGISTER_URL} from "../../tools/urls";

const ConfirmCodeWindow = ({setCurrentPage, setOTP, userData, setAuthenticated}) => {
    const [otp, setOtp] = useState(new Array(6).fill(""));
    const inputRefs = useRef([]);
    const digits = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9];
    const [error, setError] = useState(false);
    const [readyToSubmit, setReadyToSubmit] = useState(false);
    useEffect(() => {
        if (inputRefs.current[0]) {
            inputRefs.current[0].focus();
        }
    }, []);

    function validateOTP() {
        for (let char of otp) {
            if (char === "" || !(char in digits)) {
                return false;
            }
        }
        return true;
    }

    function otpToString() {
        let res = "";
        for (let char of otp) {
            res += char;
        }
        return res;
    }

    const submitHandler = () => {
        if (validateOTP()) {
            setOTP(otpToString());
            setReadyToSubmit(true);
        } else {
            setError(true);
        }
    };

    useEffect(() => {
        if (readyToSubmit) {
            fetch(REGISTER_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData)
            }).then(response => {
                if (!response.ok) {
                    throw new Error("Error creating user");
                }
                return response.json();
            }).then(data => {
                setAuthenticated(true);
                localStorage.setItem("tokens", JSON.stringify(data));
            }).catch(err => console.log(err))
                .finally(() => setReadyToSubmit(false));
        }
    }, [readyToSubmit, userData]);

    const handleKeyDown = (element, index, event) => {
        if (event.key in digits) {
            setOtp((prevState) => {
                let newOtp = [...prevState];
                newOtp[index] = event.key;
                return newOtp;
            })
            if (index < 5) {
                element.nextSibling.focus();
            }
        } else if (event.key === "Backspace") {
            if (element.value === "" && index !== 0) {
                element.previousSibling.focus();
            } else {
                setOtp((prevState) => {
                    let newOtp = [...prevState];
                    newOtp[index] = "";
                    return newOtp;
                })
            }
        }

    };

    const backToQrScan = () => {
        setCurrentPage(RegisterFlow.SECRET);
    };

    return (
        <div className="container pt-5">
            {error && <ErrorMessage
                message={"The confirmation code must be exactly 6 digits long. Please enter a valid code."}
                onClose={() => setError(false)}/>}
            <div
                className="container w-50 p-3 text-center bg-light rounded-top-5 h2 mb-0 mt-5">
                <div className="row row-cols-5 rounded-3 mt-5 ms-2">
                    <div className="col col-8 text-center d-flex justify-content-center align-items-center pe-0">
                        Enter 6 digits code from Google Authenticator
                    </div>
                    <div className="col col-3 me-3 text-center">
                        <img
                            width="100px"
                            height="100px"
                            src="/images/google-authenticator-logo.svg"
                            className="border bg-light border-1 border-dark-subtle rounded-5"
                            alt="GA logo"
                        />
                    </div>
                </div>
            </div>
            <div className="container-sm p-3 py-5 text-center bg-light rounded-bottom-5 h2 w-50 h-100">
                <div className="row rounded-3 ms-2">
                    <div className="col-10 text-center offset-1">
                        <div className="form-group d-flex justify-content-center">
                            {otp.map((data, index) => (
                                <input
                                    className="otp-input form-control mx-1 text-center fs-2"
                                    type="text"
                                    name="otp"
                                    maxLength="1"
                                    key={index}
                                    value={data}
                                    onChange={() => {
                                    }}
                                    onKeyDown={e => handleKeyDown(e.target, index, e)}
                                    onFocus={e => e.target.select()}
                                    ref={el => inputRefs.current[index] = el}
                                />
                            ))}
                        </div>
                    </div>
                </div>
            </div>
            <div className="container text-end mt-3 mr-3 w-50">
                <div className="row">
                    <button type="button" onClick={backToQrScan}
                            className="btn btn-dark fs-4 rounded-4 py-3 col-5 ">Go back
                    </button>
                    <button type="button" onClick={submitHandler}
                            className="btn btn-dark fs-4 rounded-4 py-3 col-5 offset-2">Submit
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ConfirmCodeWindow;
