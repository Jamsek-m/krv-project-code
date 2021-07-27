import { AppEnvironment } from "./environment.types";

export const environment: AppEnvironment = {
    production: true,
    apis: {
        admin: {
            baseUrl: "http://localhost:8080",
            contextPath: "/admin"
        }
    },
    auth: {
        clientId: "admin-cli",
        authorizationEndpoint: "http://localhost:8080/auth",
        redirectUri: "http://localhost:8080/admin",
        tokenEndpoint: "http://localhost:8080/token",
        scopes: ["profile", "openid", "email", "admin"]
    },
    wellKnownUrl: "/protocol/oidc/.well-known"
};
