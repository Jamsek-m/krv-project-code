import { Inject, Injectable } from "@angular/core";
import { HttpBackend, HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { ADMIN_API_URL } from "@injectables";
import { AnalyticsOverview } from "@lib";

@Injectable({
    providedIn: "root"
})
export class AnalyticsService {

    private http: HttpClient;

    constructor(@Inject(ADMIN_API_URL) private apiUrl: string,
                private httpBackend: HttpBackend) {
        this.http = new HttpClient(this.httpBackend);
    }

    public getOverview(): Observable<AnalyticsOverview> {
        const url = `${this.apiUrl}/analytics/overview`;
        return this.http.get(url).pipe(
            map(res => res as AnalyticsOverview)
        );
    }

}
