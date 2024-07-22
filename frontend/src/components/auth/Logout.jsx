import React from 'react';
import {LOGOUT_URL} from "../../tools/urls";


const Logout = ({setAuthenticated}) => {
    const logoutHandler = () => {
        if (localStorage.getItem("tokens")) {
            setAuthenticated(false);
        }
        let refreshToken = JSON.parse(localStorage.getItem("tokens")).refreshToken;
        fetch(LOGOUT_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                refreshToken: refreshToken
            })
        }).then(response => {
            if (!response.ok) {
                return response.text().then((text) => {
                    throw new Error(text)
                })
            } else {
                setAuthenticated(false);
                localStorage.removeItem("tokens");
            }
        }).catch(err => console.log(err));
    }

    return (
        <button type="button" className="btn btn-dark fs-4 rounded-4 py-3 col-4 offset-4" onClick={logoutHandler}>Logout
        </button>
    );
};

export default Logout;