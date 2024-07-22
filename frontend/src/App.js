
import AuthenticationProvider from "./components/auth/AuthenticationProvider";
import {useState} from "react";
import Main from "./components/app/Main";


function App() {
  const [authenticated, setAuthenticated] = useState(false);

  //todo: add check if user has access or refresh token in local storage and if one of them still valid

  return (
      authenticated ? <Main setAuthenticated={setAuthenticated}/> : <AuthenticationProvider setAuthenticated={setAuthenticated}/>
  );
}

export default App;
