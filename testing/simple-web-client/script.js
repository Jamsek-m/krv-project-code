const CLIENT_ID = "testni";

function getQueryParam(paramName) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(paramName);
}

class AuthState {
    static __instance = null;

    /**
     * Returns singleton instance
     * @returns {AuthState}
     */
    static getInstance() {
        if (AuthState.__instance === null) {
            AuthState.__instance = new AuthState();
        }
        return AuthState.__instance;
    }

    constructor() {
        this.authz = null;
        this.refresh = null;
    }

    /**
     * Set access token
     * @param {string | null} token
     */
    setToken(token) {
        this.authz = token;
    }

    /**
     * Access token
     * @returns {string}
     */
    getToken() {
        return this.authz;
    }

    /**
     * Set refresh token
     * @param {string | null} token
     */
    setRefreshToken(token) {
        this.refresh = token;
    }

    /**
     * Refresh token
     * @returns {string}
     */
    getRefreshToken() {
        return this.refresh;
    }
}

function exchangeCodeForToken(code) {
    const request = new Request("http://localhost:8080/protocol/oidc/token", {
        method: "POST",
        body: new URLSearchParams({
            code: code,
            grant_type: "authorization_code",
            client_id: CLIENT_ID
        }),
        headers: {
            "content-type": "application/x-www-form-urlencoded"
        }
    });

    return fetch(request)
        .then(res => res.json());
}

function init() {
    const stateElem = document.getElementById("login-state");
    const actionsElem = document.getElementById("action-container");
    const tokenElem = document.getElementById("token");
    const refreshTokenElem = document.getElementById("refresh-token");

    const authorizationCode = getQueryParam("code");
    if (authorizationCode !== null) {
        exchangeCodeForToken(authorizationCode).then(tokens => {
            AuthState.getInstance().setToken(tokens["access_token"]);
            AuthState.getInstance().setRefreshToken(tokens["refresh_token"]);
            stateElem.innerText = "Logged in!";
            const btn = document.createElement("BUTTON");
            btn.innerText = "Logout";
            btn.addEventListener("click", () => {
                AuthState.getInstance().setToken(null);
                AuthState.getInstance().setRefreshToken(null);
                window.location.reload();
            });
            actionsElem.appendChild(btn);
            tokenElem.innerText = AuthState.getInstance().getToken();
            refreshTokenElem.innerText = AuthState.getInstance().getRefreshToken();

        }).catch(err => console.error(err));
    }

    const token = AuthState.getInstance().getToken();
    if (token === null) {
        stateElem.innerText = "Not logged in!";
        const btn = document.createElement("BUTTON");
        btn.innerText = "Login";
        btn.addEventListener("click", () => {
            window.location.href = `http://localhost:8080/protocol/oidc/auth?client_id=${CLIENT_ID}&redirect_uri=http://localhost:8081`
        });
        actionsElem.appendChild(btn);
    }

    generateCodeChallenge("1234").then(res => {
        console.log(res);
    });
}

function generateCryptographicRandomString() {
    const LENGTH = 128;
    const CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    const values = new Uint32Array(LENGTH);
    crypto.getRandomValues(values);
    return values.reduce((acc, current) => {
        return acc + (CHARSET[current & CHARSET.length]);
    }, "");
}

function calculateHashWithSHA256(plainText) {
    const encoder = new TextEncoder();
    const data = encoder.encode(plainText);
    return crypto.subtle.digest("SHA-256", data);
}

function encodeBase64(text) {
    return btoa(encodeURIComponent(text).replace(/%([0-9A-F]{2})/g, (match, p1) => {
        return String.fromCharCode(parseInt(p1, 16));
    }));
}

async function generateCodeChallenge(codeVerifier) {
    const digest = await crypto.subtle.digest("SHA-256", new TextEncoder().encode(codeVerifier));
    return btoa(String.fromCharCode(...new Uint8Array(digest)))
        .replace(/=/g, '')
        .replace(/\+/g, '-')
        .replace(/\//g, '_');
}
