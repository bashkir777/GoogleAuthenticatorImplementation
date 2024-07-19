import React from 'react';
import '../styles/error-message.css'
const ErrorMessage = ({message, onClose}) => {
    return (
        <div className="alert alert-danger alert-dismissible fade show error-message" role="alert">
            {message}
            <button type="button" className="btn-close" data-bs-dismiss="alert" aria-label="Close"
                    onClick={onClose}></button>
        </div>
    );
};

export default ErrorMessage;