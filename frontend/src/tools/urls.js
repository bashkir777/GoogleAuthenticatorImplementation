//export const BACKEND_URL = process.env.BACKEND_URL;
export const BACKEND_URL = "http://localhost:8080";
export const TEST_QR_LINE = "otpauth://totp/MyApp:TestUser?secret=JBSWY3DPEHPK3PXP&issuer=MyApp"
export const SECRET_QR_URL = BACKEND_URL + "/api/v1/auth/generate-secret-qr-url/";