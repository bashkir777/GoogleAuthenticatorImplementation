import AuthenticationProvider from "./components/auth/AuthenticationProvider";
import {useEffect, useState} from "react";
import Main from "./components/app/Main";
import {refreshToken} from "./tools/utils";


function App() {
    const [authenticated, setAuthenticated] = useState(false);

    useEffect(  () => {
        const checkAuthentication = async () => {
            const isAuthenticated = await refreshToken();
            setAuthenticated(isAuthenticated);
        };
        checkAuthentication().then(r => console.log("token refreshed"));
    }, [])


    return (
        authenticated ? <Main setAuthenticated={setAuthenticated}/> :
            <AuthenticationProvider setAuthenticated={setAuthenticated}/>
    );
}

export default App;
