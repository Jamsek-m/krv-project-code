import { Inject, Injectable } from "@angular/core";
import { HttpClient, HttpResponse } from "@angular/common/http";
import { ADMIN_API_URL } from "../injectables";
import { Observable } from "rxjs";
import { JsonWebKey, PublicSigningKey, SignatureAlgorithm } from "../models";
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
        const params = {
            order: "priority DESC"
        }
        return this.http.get(url, {params}).pipe(
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

    public getPlainKey(keyId: string): Observable<string> {
        const url = `${this.apiUrl}/signing-keys/${keyId}/verification-key`;
        return this.http.get(url, {observe: "response", responseType: "text"}).pipe(
            map(res => res as HttpResponse<string>),
            map((res: HttpResponse<string>) => {
                if (res.headers.get("X-Key-Id") === keyId) {
                    return res.body!;
                }
                throw new ReferenceError("Received invalid key!");
            })
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
