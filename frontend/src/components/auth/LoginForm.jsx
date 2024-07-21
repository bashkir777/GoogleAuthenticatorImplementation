import React, {useEffect, useState} from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import {AuthenticationFlow, LoginFlow} from "../../tools/consts";
import {TFA_ENABLES_URL} from "../../tools/urls";
import ErrorMessage from "../../tools/ErrorMessage";

const LoginForm = ({setAuthenticationPage, userData, setUsername, setPassword, setCurrentPage}) => {
    const switchToRegister = (event) => {
        event.preventDefault();
        setAuthenticationPage(AuthenticationFlow.REGISTER);
    }
    const [error, setError] = useState(false);
    const [tfaEnabled, setTfaEnabled] = React.useState(null);
    const submitHandler = () => {
        if (userData.username && userData.password) {
            fetch(TFA_ENABLES_URL + userData.username)
                .then(response => {
                    if (!response.ok) {
                        setError(true);
                        setUsername('');
                        setPassword('');
                        throw new Error("Invalid username or password. Please try again.");
                    }
                    return response.json();
                }).then(data => {
                setTfaEnabled(data.tfaEnabled);
            }).catch(err => console.log(err));
        } else {
            console.log("Invalid username or password")
        }
    }
    useEffect(() => {
        if (tfaEnabled !== null) {

            if (tfaEnabled) {
                setCurrentPage(LoginFlow.CONFIRMATION_CODE);
            } else {
                console.log("");
                //todo: request to logic endpoint
            }

        }
    }, [tfaEnabled])
    return (
        <>
            {error && <ErrorMessage message="Invalid username or password. Please try again."
                                    onClose={() => setError(false)}/>}
            <section className="vh-100 gradient-custom">
                <div className="container py-3 h-100">
                    <div className="row d-flex justify-content-center align-items-center h-100">
                        <div className="col-12 col-md-8 col-lg-6 col-xl-5">
                            <div className="card bg-dark text-white" style={{borderRadius: '1rem'}}>
                                <div className="card-body p-5 text-center">
                                    <div className="mb-md-3 mt-md-4 pb-5">
                                        <h2 className="fw-bold mb-2 text-uppercase">Login</h2>
                                        <p className="text-white-50 mb-5">Please enter your username and password!</p>
                                        <div data-mdb-input-init className="form-outline form-white mb-4">
                                            <input type="text" onChange={(event) => {
                                                setUsername(event.target.value);
                                            }} value={userData.username} id="typeUsername"
                                                   className="form-control form-control-lg"/>
                                            <label className="form-label" htmlFor="typeUsername">Username</label>
                                        </div>
                                        <div data-mdb-input-init className="form-outline form-white mb-4">
                                            <input type="password" onChange={(event) => {
                                                setPassword(event.target.value);
                                            }} value={userData.password} id="typePasswordX"
                                                   className="form-control form-control-lg"/>
                                            <label className="form-label" htmlFor="typePasswordX">Password</label>
                                        </div>
                                        <p className="small mb-5 pb-lg-2"><a className="text-white-50" href="#!">Forgot
                                            password?</a></p>
                                        <button data-mdb-button-init data-mdb-ripple-init
                                                className="btn btn-outline-light btn-lg px-5" onClick={submitHandler}
                                                type="submit">Login
                                        </button>
                                    </div>
                                    <div>
                                        <p className="mb-0">Don't have an account? <a href="" onClick={switchToRegister}
                                                                                      className="text-white-50 fw-bold">Sign
                                            Up</a>
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

export default LoginForm;