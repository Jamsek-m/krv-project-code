import { AppEnvironment } from "./environment.types";

export const environment: AppEnvironment = {
    production: true,
    env: "deployment",
    apis: {
        admin: {
            baseUrl: "",
            contextPath: "/admin"
        }
    },
    auth: {
        clientId: "admin-console",
        wellKnownEndpoint: "/protocol/oidc/.well-known",
        redirectUri: "https://test.mjamsek.com/auth/callback/oidc",
        scopes: ["profile", "openid", "email", "admin"],
        postLogoutRedirectUri: "https://test.mjamsek.com/auth/",
        profileEndpoint: "https://test.mjamsek.com/user-profile"
    },
};
