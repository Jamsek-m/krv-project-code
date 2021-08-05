import { AppEnvironment } from "./environment.types";

export const environment: AppEnvironment = {
    production: true,
    apis: {
        admin: {
            baseUrl: "",
            contextPath: "/admin"
        }
    },
    auth: {
        clientId: "admin-console",
        wellKnownEndpoint: "/protocol/oidc/.well-known",
        redirectUri: "http://localhost:8080/auth/callback/oidc",
        scopes: ["profile", "openid", "email", "admin"],
        postLogoutRedirectUri: "http://localhost:8080/auth/"
    },
};
