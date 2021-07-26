// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

import { AppEnvironment } from "./environment.types";

export const environment: AppEnvironment = {
    production: false,
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
    }
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
