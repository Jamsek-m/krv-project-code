// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

import { AppEnvironment } from "./environment.types";

export const environment: AppEnvironment = {
    production: false,
    env: "dev",
    apis: {
        admin: {
            baseUrl: "http://localhost:8080",
            contextPath: "/admin"
        }
    },
    auth: {
        clientId: "admin-console",
        wellKnownEndpoint: "http://localhost:8080/protocol/oidc/.well-known",
        redirectUri: "http://localhost:4200/auth/callback/oidc",
        scopes: ["profile", "openid", "email", "admin"],
        settings: {
            checkSessionEverySeconds: 5,
            refreshTokenSecondsBefore: 10,
        },
        profileEndpoint: "http://localhost:8080/user-profile"
    },
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
