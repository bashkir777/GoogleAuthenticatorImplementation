//export const BACKEND_URL = process.env.BACKEND_URL;
export const BACKEND_URL = "http://localhost:8080";
const AUTH_URL = BACKEND_URL + "/api/v1/auth"
export const SECRET_QR_URL = AUTH_URL + "/generate-secret-qr-url/";
export const LOGIN_URL = AUTH_URL + "/login";
export const REGISTER_URL = AUTH_URL + "/register";
export const REFRESH_URL = AUTH_URL + "/refresh";