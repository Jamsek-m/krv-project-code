import { InjectionToken } from "@angular/core";
import { environment } from "../environments/environment";
import { AuthConfig } from "../environments/environment.types";


export const ADMIN_API_URL = new InjectionToken<string>("ADMIN_API_URL", {
    providedIn: "root",
    factory: () => environment.apis!.admin.baseUrl + environment.apis!.admin.contextPath,
});

export const AUTH_CONFIG = new InjectionToken<AuthConfig>("AUTH_CONFIG", {
    providedIn: "root",
    factory: () => environment.auth,
});
