import { Inject, Injectable } from "@angular/core";
import { HttpClient, HttpResponse } from "@angular/common/http";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { EntityList } from "@mjamsek/prog-utils";
import { ADMIN_API_URL } from "@injectables";
import { Client } from "@lib";
import { mapToEntityList } from "@utils";


@Injectable({
    providedIn: "root"
})
export class ClientService {

    constructor(@Inject(ADMIN_API_URL) private apiUrl: string,
                private http: HttpClient) {
    }

    public getClients(): Observable<EntityList<Client>> {
        const url = `${this.apiUrl}/clients`;
        const params = {
            limit: 10,
            offset: 0,
        };
        return this.http.get(url, {observe: "response", params}).pipe(
            map(res => res as HttpResponse<Client[]>),
            map(mapToEntityList)
        );
    }

    public getClient(clientId: string): Observable<Client> {
        const url = `${this.apiUrl}/clients/${clientId}`;
        return this.http.get(url).pipe(
            map(res => res as Client),
        );
    }

    public createClient(payload: Partial<Client>): Observable<Client> {
        const url = `${this.apiUrl}/clients`;
        return this.http.post(url, payload).pipe(
            map(res => res as Client),
        );
    }

    public patchClient(clientId: string, payload: Partial<Client>): Observable<Client> {
        const url = `${this.apiUrl}/clients/${clientId}`;
        return this.http.patch(url, payload).pipe(
            map(res => res as Client)
        );
    }

    public regenerateClientSecret(clientId: string): Observable<void> {
        const url = `${this.apiUrl}/clients/${clientId}/secret`;
        return this.http.post(url, null).pipe(
            map(res => res as unknown as void)
        );
    }

    public changeClientStatus(clientId: string, newStatus: boolean): Observable<void> {
        const url = `${this.apiUrl}/clients/${clientId}/${newStatus ? "enable" : "disable"}`;
        let request$ = this.http.patch(url, null);
        if (!newStatus) {
            request$ = this.http.delete(url);
        }
        return request$.pipe(
            map(res => res as unknown as void),
        );
    }

}
