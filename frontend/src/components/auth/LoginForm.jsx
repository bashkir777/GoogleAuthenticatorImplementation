import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import {AuthenticationFlow} from "../../tools/consts";
const LoginForm = ({setAuthenticationPage}) => {
    const switchToRegister = (event)=>{
        event.preventDefault();
        setAuthenticationPage(AuthenticationFlow.REGISTER);
    }

    return (
        <section className="vh-100 gradient-custom">
            <div className="container py-3 h-100">
                <div className="row d-flex justify-content-center align-items-center h-100">
                    <div className="col-12 col-md-8 col-lg-6 col-xl-5">
                        <div className="card bg-dark text-white" style={{ borderRadius: '1rem' }}>
                            <div className="card-body p-5 text-center">
                                <div className="mb-md-3 mt-md-4 pb-5">
                                    <h2 className="fw-bold mb-2 text-uppercase">Login</h2>
                                    <p className="text-white-50 mb-5">Please enter your username and password!</p>
                                    <div data-mdb-input-init className="form-outline form-white mb-4">
                                        <input type="text" id="typeUsername" className="form-control form-control-lg"/>
                                        <label className="form-label" htmlFor="typeUsername">Username</label>
                                    </div>
                                    <div data-mdb-input-init className="form-outline form-white mb-4">
                                        <input type="password" id="typePasswordX"
                                               className="form-control form-control-lg"/>
                                        <label className="form-label" htmlFor="typePasswordX">Password</label>
                                    </div>
                                    <p className="small mb-5 pb-lg-2"><a className="text-white-50" href="#!">Forgot
                                        password?</a></p>
                                    <button data-mdb-button-init data-mdb-ripple-init
                                            className="btn btn-outline-light btn-lg px-5" type="submit">Login
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
    );
};

export default LoginForm;