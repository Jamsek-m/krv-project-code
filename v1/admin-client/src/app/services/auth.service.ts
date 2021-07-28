import { HttpClient } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { AUTH_CONFIG } from "../injectables";
import { AuthConfig } from "../../environments/environment.types";
import { Observable } from "rxjs";
import { take, tap } from "rxjs/operators";
import { createPKCEChallenge } from "@utils";
import { PKCEChallenge } from "@lib";

@Injectable({
    providedIn: "root"
})
export class AuthService {

    public static readonly PKCE_KEY = "krv.auth.pkce_challenge";

    private accessToken: string | null;

    constructor(@Inject(AUTH_CONFIG) private authConfig: AuthConfig,
                private http: HttpClient) {
    }

    public login() {

        createPKCEChallenge(PKCEChallenge.PKCEMethod.S256)
            .pipe(take(1))
            .subscribe((challenge: PKCEChallenge) => {
                console.log(challenge);
                sessionStorage.setItem(AuthService.PKCE_KEY, challenge.code_verifier);
                const scopes = this.authConfig.scopes.join(" ");
                window.location.href = this.buildQueryUrl(this.authConfig.authorizationEndpoint, {
                    client_id: this.authConfig.clientId,
                    redirect_uri: this.authConfig.redirectUri,
                    scope: scopes,
                    code_challenge: challenge.code_challenge,
                    code_challenge_method: challenge.code_challenge_method
                });
            });
    }

    public exchangeAuthorizationCode(code: string): Observable<any> {
        const url = `${this.authConfig.tokenEndpoint}`;
        const formData = new URLSearchParams();
        formData.set("client_id", this.authConfig.clientId);
        formData.set("code", code);
        formData.set("grant_type", "authorization_code");

        const verifier = sessionStorage.getItem(AuthService.PKCE_KEY);
        if (verifier === null) {
            throw new Error("No PKCE challenge! Code cannot be exchanged!");
        }
        formData.set("code_verifier", verifier);

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
