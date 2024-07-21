/**
 * Parses an otpauth URL and returns an object with the parsed information.
 *
 * @param {string} otpauthUrl - The otpauth URL to parse.
 * @returns {object} - An object containing the appName, username, secret, and issuer.
 */
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