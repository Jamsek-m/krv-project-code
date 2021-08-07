import { AppEnvironment } from "./environment.types";

export const environment: AppEnvironment = {
    production: true,
    env: "prod",
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
        profileEndpoint: "http://localhost:8080/user-profile"
    },
};
