import { from, Observable, of } from "rxjs";
import { map } from "rxjs/operators";
import { PKCEChallenge, TokenInfo } from "@lib";

export function generateRandomData(len: number): Uint8Array {
    let arr = new Uint8Array(len);
    crypto.getRandomValues(arr);
    return arr;
}

export function generateRandomString(len: number): string {
    const ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    const randomData = generateRandomData(len);
    let chars = new Array(len);
    for (let i = 0; i < chars.length; i++) {
        chars[i] = ALPHABET.charCodeAt(randomData[i] % ALPHABET.length);
    }
    return String.fromCharCode.apply(null, chars);
}

export function generateCodeVerifier(len: number): string {
    return generateRandomString(len);
}

type CodeChallengeResult = {
    code_challenge: string;
    code_challenge_method: PKCEChallenge.PKCEMethod;
};

export function generateCodeChallenge(codeVerifier: string, method: PKCEChallenge.PKCEMethod): Observable<CodeChallengeResult> {
    if (method === "S256") {
        let hashBytes = new TextEncoder().encode(codeVerifier);
        return from(crypto.subtle.digest("SHA-256", hashBytes)).pipe(
            map((digest: ArrayBuffer) => {
                return base64UrlEncode(String.fromCharCode(...new Uint8Array(digest)));
            }),
            map((codeChallenge: string) => {
                return {
                    code_challenge: codeChallenge,
                    code_challenge_method: PKCEChallenge.PKCEMethod.S256,
                };
            })
        );
    }
    return of({
        code_challenge_method: PKCEChallenge.PKCEMethod.plain,
        code_challenge: codeVerifier,
    });
}

export function createPKCEChallenge(method: PKCEChallenge.PKCEMethod = "S256"): Observable<PKCEChallenge> {
    const codeVerifier = generateCodeVerifier(80);
    return generateCodeChallenge(codeVerifier, method).pipe(
        map((result: CodeChallengeResult) => {
            return {
                code_challenge: result.code_challenge,
                code_challenge_method: result.code_challenge_method,
                code_verifier: codeVerifier,
            };
        })
    );
}

export function base64UrlEncode(str: string): string {
    return btoa(str)
        .replace(/\+/g, "-")
        .replace(/\//g, "_")
        .replace(/=+$/g, "");
}

export function base64UrlDecode(str: string): string {
    return atob(str
        .replace(/-/g, "+")
        .replace(/_/g, "/")
    );
}

export function epochSecondsToDate(epochSeconds: number): Date {
    const date = new Date(0);
    date.setUTCSeconds(epochSeconds);
    return date;
}

export function parseTokenPayload(token: string): TokenInfo {
    const tokenParts = token.split(".");
    if (tokenParts.length !== 3) {
        throw new Error("Malformed token!");
    }
    try {
        const decodedPayload = base64UrlDecode(tokenParts[1])
        const parsedPayload = JSON.parse(decodedPayload);

        return {
            subject: parsedPayload["sub"],
            username: parsedPayload["preferred_username"],
            email: parsedPayload["email"],
            name: parsedPayload["name"],
            expiresAt: epochSecondsToDate(parsedPayload["exp"]),
            sessionState: parsedPayload["session_state"],
        }
    } catch (err) {
        console.error(err);
        throw err;
    }
}
