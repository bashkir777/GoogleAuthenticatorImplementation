import React, {useState} from 'react';
import {AuthenticationFlow, RegisterFlow} from "../../tools/consts";
import {IS_USERNAME_FREE, REGISTER_URL} from "../../tools/urls";
import {postUserData} from "../../tools/utils";
import ErrorMessage from "../../tools/ErrorMessage";

const RegisterForm = ({
                          changeUsername,
                          setUsername,
                          changePassword,
                          changeFirstname,
                          changeLastname,
                          setAuthenticationPage,
                          setCurrentPage,
                          userData,
                          setAuthenticated,
                          validateUserData,
                          setTfaEnabled,
                          cleanError
                      }) => {
    const [error, setError] = useState(false);
    const [message, setMessage] = useState("");
    const switchToLogin = (event) => {
        event.preventDefault();
        setAuthenticationPage(AuthenticationFlow.LOGIN);
        cleanError();
    }
    const switchToInstallation = async (event) => {
        event.preventDefault();
        if (validateUserData(userData) === true) {
            let res = await fetch(IS_USERNAME_FREE + userData.username, {
                method: "GET"
            })

            if (res.ok) {
                let data = await res.json();
                if (data.isFree) {
                    if (userData.tfaEnabled === true) {
                        setCurrentPage(RegisterFlow.INSTALLATION)
                        cleanError();
                    } else {
                        postUserData(REGISTER_URL, userData).then(data => {
                            localStorage.setItem("tokens", JSON.stringify(data));
                            console.log(data);
                            setAuthenticated(true);
                        }).catch(err => console.log(err));
                    }
                } else {
                    setMessage(`Username ${userData.username} is unavailable. Please try another one.`);
                    setUsername("");
                    setError(true);
                }
            } else {
                setMessage(`Registration is currently unavailable. Please try later.`);
                setError(true);
            }
        }
    }

    return (
        <>
            {error && <ErrorMessage message={message} onClose={() => setError(false)}/>}
            <section className="vh-100 gradient-custom">
                <div className="container py-3 h-100">
                    <div className="row d-flex justify-content-center align-items-center h-100">
                        <div className="col-12 col-md-8 col-lg-6 col-xl-5">
                            <div className="card bg-dark text-white" style={{borderRadius: '1rem'}}>
                                <div className="card-body p-5 text-center">
                                    <div className="mb-md-3 mt-md-1 pb-3">
                                        <h2 className="fw-bold mb-2 text-uppercase">Register</h2>
                                        <p className="text-white-50 mb-3">Please fill in all required fields!</p>
                                        <div data-mdb-input-init className="form-outline form-white mb-2">
                                            <input type="text" id="typeUsername"
                                                   className="form-control form-control-lg"
                                                   onChange={changeUsername} value={userData.username}/>
                                            <label className="form-label" htmlFor="typeUsername">Username</label>
                                        </div>
                                        <div data-mdb-input-init className="form-outline form-white mb-2">
                                            <input type="password" id="typePasswordX"
                                                   className="form-control form-control-lg" value={userData.password}
                                                   onChange={changePassword}/>
                                            <label className="form-label" htmlFor="typePasswordX">Password</label>
                                        </div>
                                        <div data-mdb-input-init className="form-outline form-white mb-2">
                                            <input type="text" id="typeUsername"
                                                   className="form-control form-control-lg"
                                                   onChange={changeFirstname} value={userData.firstname}/>
                                            <label className="form-label" htmlFor="typeUsername">Firstname</label>
                                        </div>
                                        <div data-mdb-input-init className="form-outline form-white mb-2">
                                            <input type="text" id="typeLastname"
                                                   value={userData.lastname}
                                                   className="form-control form-control-lg" onChange={changeLastname}/>
                                            <label className="form-label" htmlFor="typeLastname">Lastname</label>
                                        </div>
                                        <div className="form-check form-switch mb-4 container ">
                                            <div className="row">
                                                <div className="form-check-label fs-3 col-5 offset-2">Enable 2FA</div>
                                                <div className="col col-1 offset-2">
                                                    <input className="form-check-input fs-3" type="checkbox"
                                                           id="enable_tfa" defaultChecked onClick={
                                                        (event) => {
                                                            setTfaEnabled(event.target.checked)
                                                        }
                                                    }/>
                                                </div>
                                            </div>
                                        </div>
                                        <button data-mdb-button-init data-mdb-ripple-init
                                                onClick={switchToInstallation}
                                                className="btn btn-outline-light btn-lg px-5" type="submit">Next
                                        </button>
                                    </div>
                                    <div>
                                        <p className="mb-0">Want to go back to login form?
                                            <a href=""
                                               onClick={switchToLogin}
                                               className="ps-1 text-white-50 fw-bold">Back</a>
                                        </p>
                                    </div>

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </>
    );
};

export default RegisterForm;