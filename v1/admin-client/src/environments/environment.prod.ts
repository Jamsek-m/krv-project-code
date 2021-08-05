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
        clientId: "admin-console",
        wellKnownEndpoint: "/protocol/oidc/.well-known",
        redirectUri: "http://localhost:8080/admin",
        scopes: ["profile", "openid", "email", "admin"],
        postLogoutRedirectUri: "http://localhost:4200"
    },
};
