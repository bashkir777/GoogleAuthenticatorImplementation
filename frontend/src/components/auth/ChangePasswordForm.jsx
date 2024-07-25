import React, {useState} from 'react';
import {LoginFlow} from "../../tools/consts";
import ErrorMessage from "../../tools/ErrorMessage";
import {RESET_PASSWORD_URL} from "../../tools/urls";

const ChangePasswordForm = ({setCurrentPage, setSubmitURL, setUserData, userData, setPassword}) => {
    const [error, setError] = useState(false);
    const [message, setMessage] = useState('');

    const validatePassword = (password) =>{
        if(password === null || password === undefined){
            return false;
        }
        if (password.length < 6) {
            return false;
        }
        return true;
    }

    const submitHandler = () => {
        if(!validatePassword(userData.password)){
            setMessage("Password must be at list 6 characters long");
            setError(true);
        }else{
            setSubmitURL(RESET_PASSWORD_URL);
            setUserData({
                username: userData.username,
                newPassword: userData.password,
                otp: ''
            });
            setCurrentPage(LoginFlow.CONFIRMATION_CODE);
        }
    };
    const switchToLogin =() =>{
        setCurrentPage(LoginFlow.LOGIN)
    }

    return (
        <>
            {error && <ErrorMessage message={message} onClose={()=>{setError(false)}} />}
            <section className="vh-100 gradient-custom">
                <div className="container py-3 h-100">
                    <div className="row d-flex justify-content-center align-items-center h-100">
                        <div className="col-12 col-md-8 col-lg-6 col-xl-5">
                            <div className="card bg-dark text-white" style={{borderRadius: '1rem'}}>
                                <div className="card-body p-5 text-center">
                                    <div className="mb-md-3 mt-md-4 pb-0">
                                        <h2 className="fw-bold mb-2 text-uppercase">Change password</h2>
                                        <p className="text-white-50 mb-5">Please come up with a new password!</p>
                                        <div data-mdb-input-init className="form-outline form-white mb-4">
                                            <input type="text" readOnly={true} defaultValue={userData.username}
                                                   id="typeUsername"
                                                   className="form-control form-control-lg bg-dark-subtle"/>
                                            <label className="form-label" htmlFor="typeUsername">Username</label>
                                        </div>
                                        <div data-mdb-input-init className="form-outline form-white mb-4">
                                            <input type="password" onChange={(event) => {
                                                setPassword(event.target.value);
                                            }} value={userData.password} id="typePasswordX"
                                                   className="form-control form-control-lg"/>
                                            <label className="form-label" htmlFor="typePasswordX">New password</label>
                                        </div>
                                        <button data-mdb-button-init data-mdb-ripple-init
                                                className="btn btn-outline-light btn-lg px-5" onClick={submitHandler}
                                                type="submit">Change password
                                        </button>
                                        <div className="mt-5">
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
                </div>
            </section>
        </>
    );
};

export default ChangePasswordForm;