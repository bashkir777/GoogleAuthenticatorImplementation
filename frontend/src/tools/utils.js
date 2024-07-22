import {jwtDecode} from "jwt-decode";
import {REFRESH_URL} from "./urls";

export function parseOtpAuthUrl(otpauthUrl) {
    const regex = /^otpauth:\/\/totp\/([^:]+):([^?]+)\?secret=([^&]+)&issuer=([^&]+)$/;
    const match = otpauthUrl.match(regex);

    if (match) {
        return {
            appName: match[1],
            username: match[2],
            secret: match[3],
            issuer: match[4]
        };
    } else {
        throw new Error("Invalid otpauth URL format");
    }
}

export function isTokenExpired(token) {
    try {
        const decodedToken = jwtDecode(token);
        const currentTime = Math.floor(Date.now() / 1000);
        return decodedToken.exp < currentTime;
    } catch (error) {
        console.error('Invalid token:', error);
        return true;
    }
}

export async function refreshToken() {
    if (localStorage.getItem("tokens") !== null) {
        let tokens = JSON.parse(localStorage.getItem("tokens"));
        const refreshToken = tokens.refreshToken;
        const accessToken = tokens.accessToken;

        if (!isTokenExpired(accessToken)) {
            return true;
        } else if (!isTokenExpired(refreshToken)) {
            try {
                const response = await fetch(REFRESH_URL, {
                    method: "POST",
                    body: JSON.stringify({refreshToken: refreshToken}),
                    headers: {
                        "Content-Type": "application/json"
                    }
                });

                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(errorText);
                }

                const data = await response.json();
                localStorage.setItem("tokens", JSON.stringify({
                    refreshToken: refreshToken,
                    accessToken: data.accessToken
                }));
                return true;
            } catch (error) {
                console.error('Error refreshing token:', error);
                return false;
            }
        } else {
            return false;
        }
    } else {
        return false;
    }
}
