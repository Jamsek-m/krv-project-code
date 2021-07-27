import { Inject, Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { ADMIN_API_URL } from "../injectables";
import { Observable } from "rxjs";
import { JsonWebKey, JWKS, PublicSigningKey, SignatureAlgorithm } from "../models";
import { map } from "rxjs/operators";

@Injectable({
    providedIn: "root"
})
export class KeysService {

    constructor(@Inject(ADMIN_API_URL) private apiUrl: string,
                private http: HttpClient) {
    }

    public getKeys(): Observable<PublicSigningKey[]> {
        const url = `${this.apiUrl}/signing-keys`;
        return this.http.get(url).pipe(
            map(res => res as PublicSigningKey[])
        );
    }

    public createKey(algorithm: SignatureAlgorithm): Observable<JsonWebKey> {
        const url = `${this.apiUrl}/signing-keys`;
        const payload = {
            algorithm,
        }
        return this.http.post(url, payload).pipe(
            map(res => res as JsonWebKey)
        );
    }

    public setClientSigningKey(clientId: string, algorithm: SignatureAlgorithm): Observable<void> {
        const url = `${this.apiUrl}/signing-keys/${clientId}`;
        const payload = {
            algorithm,
        }
        return this.http.patch(url, payload).pipe(
            map(res => res as unknown as void)
        );
    }
}
