import React, {useState} from 'react';
import {ChangePasswordFlow, LoginFlow} from "../../tools/consts";
import {TFA_ENABLES_URL} from "../../tools/urls";
import ErrorMessage from "../../tools/ErrorMessage";

const ChangePasswordEnterUsernameForm = ({setCurrentPage, userData, setUsername, setCurrentPasswordChangePage}) => {
    const [error, setError] = useState(false);
    const [message, setMessage] = useState('');
    const submitHandler = () => {
        fetch(TFA_ENABLES_URL + userData.username)
            .then(response => {
                if (!response.ok) {
                    setMessage("Invalid username. Please try again.")
                    setError(true);
                    setUsername('');
                    throw new Error("Invalid username. Please try again.");
                }
                return response.json();
            }).then(data => {
            if(data.tfaEnabled === true){
                setCurrentPasswordChangePage(ChangePasswordFlow.CHANGE_PASSWORD)
            }else{
                setMessage('Password change requires enabled TFA');
                setError(true);
            }
        }).catch(err => console.log(err));
    };
    const switchToLogin =() =>{
        setCurrentPage(LoginFlow.LOGIN);
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
                                    <div className="mb-md-3 mt-md-4 pb-0">
                                        <h2 className="fw-bold mb-2 text-uppercase">enter your username</h2>
                                        <p className="text-white-50 mb-5">Warning! Password change requires enabled TFA</p>
                                        <div data-mdb-input-init className="form-outline form-white mb-4">
                                            <input type="text" defaultValue={userData.username}
                                                   id="typeUsername"
                                                   className="form-control form-control-lg"/>
                                            <label className="form-label" htmlFor="typeUsername">Username</label>
                                        </div>
                                        <button data-mdb-button-init data-mdb-ripple-init
                                                className="btn btn-outline-light btn-lg px-5" onClick={submitHandler}
                                                type="submit">Next
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

export default ChangePasswordEnterUsernameForm;