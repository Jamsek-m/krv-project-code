import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, of } from "rxjs";
import { map } from "rxjs/operators";
import { WellKnownConfig } from "../models";
import { environment } from "../../environments/environment";

@Injectable({
    providedIn: "root",
})
export class ProviderContext {

    private $cache: WellKnownConfig | null = null;

    constructor(private http: HttpClient) {

    }

    public getWellKnownConfig(): Observable<WellKnownConfig> {
        if (this.$cache === null) {
            return this.fetchWellKnownConfig();
        }
        return of(this.$cache);
    }

    private fetchWellKnownConfig(): Observable<WellKnownConfig> {
        return this.http.get(environment.wellKnownUrl).pipe(
            map(res => res as WellKnownConfig)
        );
    }

}
