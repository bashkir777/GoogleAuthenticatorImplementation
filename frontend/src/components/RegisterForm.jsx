import React from 'react';

const RegisterForm = ({setShowLoginForm}) => {

    const switchToLogin = (event) =>{
        event.preventDefault();
        setShowLoginForm(true);
    }

    return (
        <section className="vh-100 gradient-custom">
            <div className="container py-3 h-100">
                <div className="row d-flex justify-content-center align-items-center h-100">
                    <div className="col-12 col-md-8 col-lg-6 col-xl-5">
                        <div className="card bg-dark text-white" style={{borderRadius: '1rem'}}>
                            <div className="card-body p-5 text-center">
                                <div className="mb-md-3 mt-md-4 pb-3">
                                    <h2 className="fw-bold mb-2 text-uppercase">Register</h2>
                                    <p className="text-white-50 mb-5">Please fill in all required fields!</p>
                                    <div data-mdb-input-init className="form-outline form-white mb-2">
                                        <input type="text" id="typeUsername" className="form-control form-control-lg"/>
                                        <label className="form-label" htmlFor="typeUsername">Username</label>
                                    </div>
                                    <div data-mdb-input-init className="form-outline form-white mb-2">
                                        <input type="password" id="typePasswordX"
                                               className="form-control form-control-lg"/>
                                        <label className="form-label" htmlFor="typePasswordX">Password</label>
                                    </div>
                                    <div data-mdb-input-init className="form-outline form-white mb-2">
                                        <input type="text" id="typeUsername" className="form-control form-control-lg"/>
                                        <label className="form-label" htmlFor="typeUsername">Firstname</label>
                                    </div>
                                    <div data-mdb-input-init className="form-outline form-white mb-2">
                                        <input type="password" id="typePasswordX"
                                               className="form-control form-control-lg"/>
                                        <label className="form-label" htmlFor="typePasswordX">Lastname</label>
                                    </div>

                                    <button data-mdb-button-init data-mdb-ripple-init
                                            className="btn btn-outline-light btn-lg px-5" type="submit">Create an
                                        account
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
    );
};

export default RegisterForm;