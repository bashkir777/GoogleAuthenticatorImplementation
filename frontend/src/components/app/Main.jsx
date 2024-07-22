import React from 'react';
import Logout from "../auth/Logout";

const Main = ({setAuthenticated}) => {
    return (
        <div className="mt-5 pt-5 container-fluid">
            <Logout setAuthenticated={setAuthenticated}/>
        </div>
    );
};

export default Main;