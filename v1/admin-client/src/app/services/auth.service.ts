import { HttpClient } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { AUTH_CONFIG } from "../injectables";
import { AuthConfig } from "../../environments/environment.types";
import { Observable } from "rxjs";
import { tap } from "rxjs/operators";

@Injectable({
    providedIn: "root"
})
export class AuthService {

    private accessToken: string | null;

    constructor(@Inject(AUTH_CONFIG) private authConfig: AuthConfig,
                private http: HttpClient) {
    }

    public login() {
        const scopes = this.authConfig.scopes.join(" ");
        window.location.href = this.buildQueryUrl(this.authConfig.authorizationEndpoint, {
            client_id: this.authConfig.clientId,
            redirect_uri: this.authConfig.redirectUri,
            scope: scopes,
        });
    }

    public exchangeAuthorizationCode(code: string): Observable<any> {
        const url = `${this.authConfig.tokenEndpoint}`;
        const formData = new URLSearchParams();
        formData.set("client_id", this.authConfig.clientId);
        formData.set("code", code);
        formData.set("grant_type", "authorization_code");

        console.log(formData);
        return this.http.post(url, formData, {
            headers: {
                "content-type": "application/x-www-form-urlencoded",
                "accept": "application/json"
            }
        }).pipe(
            tap((res) => {
                console.log(res);
                this.accessToken = (res as any)["access_token"];
            })
        );
    }

    public getAccessToken(): string | null {
        return this.accessToken;
    }

    private buildQueryUrl(url: string, params: { [key: string]: string }): string {
        const urlObj = new URL(url);
        Object.keys(params).forEach(param => {
            urlObj.searchParams.append(param, params[param]);
        });
        return urlObj.toString();
    }

}
