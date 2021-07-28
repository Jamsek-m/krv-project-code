import { Inject, Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, of } from "rxjs";
import { map } from "rxjs/operators";
import { WellKnownConfig } from "@lib";
import { AUTH_CONFIG } from "../injectables";
import { AuthConfig } from "../../environments/environment.types";

@Injectable({
    providedIn: "root",
})
export class ProviderContext {

    private $cache: WellKnownConfig | null = null;

    constructor(@Inject(AUTH_CONFIG) private authConfig: AuthConfig,
                private http: HttpClient) {

    }

    public getWellKnownConfig(): Observable<WellKnownConfig> {
        if (this.$cache === null) {
            return this.fetchWellKnownConfig();
        }
        return of(this.$cache);
    }

    private fetchWellKnownConfig(): Observable<WellKnownConfig> {
        return this.http.get(this.authConfig.wellKnownEndpoint).pipe(
            map(res => res as WellKnownConfig)
        );
    }

}
