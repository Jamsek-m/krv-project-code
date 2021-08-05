import { Inject, Injectable } from "@angular/core";
import { HttpBackend, HttpClient } from "@angular/common/http";
import { BehaviorSubject, Observable } from "rxjs";
import { filter, map, take } from "rxjs/operators";
import { WellKnownConfig } from "@lib";
import { AUTH_CONFIG } from "@injectables";
import { AuthConfig } from "@environment/environment.types";

@Injectable({
    providedIn: "root",
})
export class ProviderContext {

    private cache$: BehaviorSubject<WellKnownConfig | null> = new BehaviorSubject<WellKnownConfig | null>(null);

    private http: HttpClient;

    constructor(@Inject(AUTH_CONFIG) private authConfig: AuthConfig,
                private httpBackend: HttpBackend) {
        this.http = new HttpClient(httpBackend);
    }

    public loadWellKnownConfig(): void {
        this.http.get(this.authConfig.wellKnownEndpoint).pipe(
            map(res => res as WellKnownConfig),
            take(1),
        ).subscribe((config: WellKnownConfig) => {
            this.cache$.next(config);
        });
    }

    public getWellKnownConfig(): Observable<WellKnownConfig> {
        return this.cache$.pipe(
            filter((config: WellKnownConfig | null) => {
                return config !== null;
            }),
            map(config => config as WellKnownConfig)
        );
    }
}
