import { Inject, Injectable } from "@angular/core";
import { ADMIN_API_URL } from "../injectables";
import { HttpClient, HttpResponse } from "@angular/common/http";
import { EntityList } from "@mjamsek/prog-utils";
import { Client } from "../models";
import { map } from "rxjs/operators";
import { mapToEntityList } from "../utils/list.utils";
import { Observable } from "rxjs";


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
}
