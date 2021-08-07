import { Inject, Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { ADMIN_API_URL } from "@injectables";
import { Settings, SettingsResponse } from "@lib";

@Injectable({
    providedIn: "root"
})
export class SettingsService {

    constructor(@Inject(ADMIN_API_URL) private apiUrl: string,
                private http: HttpClient) {
    }

    public getSettings(keys: string[]): Observable<SettingsResponse> {
        const url = `${this.apiUrl}/settings`;
        return this.http.post(url, keys).pipe(
            map(res => res as SettingsResponse)
        );
    }

    public updateSettings(key: string, settings: Partial<Settings>): Observable<void> {
        const url = `${this.apiUrl}/settings/${key}`;
        return this.http.put(url, settings).pipe(
            map(res => res as unknown as void)
        );
    }

}
