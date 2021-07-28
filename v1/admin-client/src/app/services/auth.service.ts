import { HttpClient } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { AUTH_CONFIG } from "../injectables";
import { AuthConfig } from "../../environments/environment.types";
import { Observable } from "rxjs";
import { map, switchMap, take, tap } from "rxjs/operators";
import { createPKCEChallenge } from "@utils";
import { PKCEChallenge, WellKnownConfig } from "@lib";
import { ProviderContext } from "@context";

@Injectable({
    providedIn: "root"
})
export class AuthService {

    public static readonly PKCE_KEY = "krv.auth.pkce_challenge";

    private accessToken: string | null;

    constructor(@Inject(AUTH_CONFIG) private authConfig: AuthConfig,
                private provider: ProviderContext,
                private http: HttpClient) {
    }

    public login() {
        this.provider.getWellKnownConfig().pipe(
            switchMap((config: WellKnownConfig) => {
                return createPKCEChallenge(PKCEChallenge.PKCEMethod.S256).pipe(
                    map((challenge: PKCEChallenge) => {
                        return {
                            challenge,
                            config,
                        }
                    })
                )
            }),
            take(1)
        ).subscribe(request => {
            const {config, challenge} = request;
            console.log(challenge);
            sessionStorage.setItem(AuthService.PKCE_KEY, challenge.code_verifier);
            window.location.href = this.buildQueryUrl(config.authorization_endpoint, {
                client_id: this.authConfig.clientId,
                redirect_uri: this.authConfig.redirectUri,
                scope: this.authConfig.scopes.join(" "),
                code_challenge: challenge.code_challenge,
                code_challenge_method: challenge.code_challenge_method
            });
        });
    }

    public exchangeAuthorizationCode(code: string): Observable<any> {
        return this.provider.getWellKnownConfig().pipe(
            switchMap((config: WellKnownConfig) => {
                const url = `${config.token_endpoint}`;
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
                })
            }),
            tap((res) => {
                console.log(res);
                this.accessToken = (res as any)["access_token"];
            })
        )
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
