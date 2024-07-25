export const BACKEND_URL = "";
//export const BACKEND_URL = "http://localhost:8080";
const AUTH_URL = BACKEND_URL + "/api/v1/auth"
export const SECRET_QR_URL = AUTH_URL + "/generate-secret-qr-url/";
export const LOGIN_URL = AUTH_URL + "/login";
export const LOGOUT_URL = AUTH_URL + "/logout";
export const REGISTER_URL = AUTH_URL + "/register";
export const REFRESH_URL = AUTH_URL + "/refresh";
export const TFA_ENABLES_URL = AUTH_URL + "/tfa-enabled/";