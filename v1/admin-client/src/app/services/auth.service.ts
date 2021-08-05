import { HttpBackend, HttpClient } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { AUTH_CONFIG } from "@injectables";
import { AuthConfig } from "@environment/environment.types";
import { Observable, of, throwError } from "rxjs";
import { filter, map, switchMap, take, tap } from "rxjs/operators";
import { createPKCEChallenge, parseTokenPayload } from "@utils";
import { AuthState, AuthStateStatus, LoginRequest, PKCEChallenge, TokenResponse, WellKnownConfig } from "@lib";
import { AuthContext, ProviderContext } from "@context";


@Injectable({
    providedIn: "root"
})
export class AuthService {

    public static readonly PKCE_KEY = "krv.auth.pkce_challenge";
    public static readonly SILENT_LOGIN_KEY = "krv.auth.silent_login_mode";

    private http: HttpClient;

    constructor(@Inject(AUTH_CONFIG) private authConfig: AuthConfig,
                private provider: ProviderContext,
                private auth: AuthContext,
                private httpBackend: HttpBackend) {
        this.http = new HttpClient(httpBackend);
    }

    private prepareAuthorizationFlow(): Observable<LoginRequest> {
        return this.provider.getWellKnownConfig().pipe(
            switchMap((config: WellKnownConfig) => {
                return createPKCEChallenge(PKCEChallenge.PKCEMethod.S256).pipe(
                    map((challenge: PKCEChallenge) => {
                        return {
                            pkceChallenge: challenge,
                            wellKnown: config,
                        };
                    })
                )
            })
        );
    }

    public onNoSessionError(): void {
        this.auth.onNoSession();
    }

    public silentLogin() {
        /*const queryParams = new URLSearchParams(window.location.search);
        const code = queryParams.get("code");
        if (code === null) {

        }*/

        this.auth.getAuthState().pipe(
            filter((state: AuthState) => {
                return state.status === AuthStateStatus.NO_TOKENS;
            }),
            switchMap(() => {
                return this.prepareAuthorizationFlow();
            }),
            take(1)
        ).subscribe((request: LoginRequest) => {
            const {wellKnown, pkceChallenge} = request;
            sessionStorage.setItem(AuthService.PKCE_KEY, pkceChallenge.code_verifier);
            window.location.href = this.buildQueryUrl(wellKnown.authorization_endpoint, {
                client_id: this.authConfig.clientId,
                redirect_uri: this.authConfig.redirectUri,
                prompt: "none",
                code_challenge: pkceChallenge.code_challenge,
                code_challenge_method: pkceChallenge.code_challenge_method
            });
        });
    }

    public login() {
        this.prepareAuthorizationFlow().pipe(
            take(1)
        ).subscribe(request => {
            const {wellKnown, pkceChallenge} = request;
            sessionStorage.setItem(AuthService.PKCE_KEY, pkceChallenge.code_verifier);

            window.location.href = this.buildQueryUrl(wellKnown.authorization_endpoint, {
                client_id: this.authConfig.clientId,
                redirect_uri: this.authConfig.redirectUri,
                scope: this.authConfig.scopes.join(" "),
                code_challenge: pkceChallenge.code_challenge,
                code_challenge_method: pkceChallenge.code_challenge_method
            });
        });
    }

    public logout(): void {
        this.auth.onLogout();
        this.provider.getWellKnownConfig().pipe(
            take(1)
        ).subscribe((config: WellKnownConfig) => {
            window.location.href = config.end_session_endpoint + "?post_logout_redirect_uri=" + this.authConfig.postLogoutRedirectUri;
        });
    }

    public getAuthState(): Observable<AuthState> {
        return this.auth.getAuthState();
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
            map(res => res as TokenResponse),
            tap((tokens: TokenResponse) => {
                this.auth.onAuthentication(tokens);
            })
        )
    }

    public getAccessToken(): Observable<string | null> {
        return this.auth.getAuthState().pipe(
            switchMap((state: AuthState) => {
                if (state.status === AuthStateStatus.AUTHENTICATED) {
                    const now = new Date();
                    const expiresAt = state.parsedAccessToken.expiresAt;

                    const timeUntilExpiry = expiresAt.getTime() - now.getTime();
                    if (timeUntilExpiry <= this.getReshreshSecondsBefore()) {
                        // token is about to expire
                        const refreshTokenPayload = parseTokenPayload(state.refreshToken);
                        const timeUntilRefreshExpiry = refreshTokenPayload.expiresAt.getTime() - now.getTime();
                        if (timeUntilRefreshExpiry > this.getReshreshSecondsBefore()) {
                            // Refresh token is still valid, update tokens
                            return this.refreshToken().pipe(
                                map((tokens: TokenResponse) => {
                                    return tokens.access_token;
                                })
                            );
                        } else {
                            // Refresh token is also expired, login required
                            return throwError(new Error("Tokens expired, relogin required"));
                        }
                    }
                    // Token is still valid
                    return of(state.accessToken);
                }
                return throwError(new Error("Unauthenticated!"));
            })
        );
    }

    public refreshToken(): Observable<TokenResponse> {
        return this.provider.getWellKnownConfig().pipe(
            switchMap((config: WellKnownConfig) => {
                return this.auth.getRefreshToken().pipe(
                    map((refreshToken: string | null) => {
                        return {
                            token: refreshToken,
                            config,
                        }
                    })
                );
            }),
            switchMap(request => {
                const {config, token} = request;

                if (token === null) {
                    return throwError(new Error("No refresh token!"));
                }

                const url = `${config.token_endpoint}`;
                const formData = new URLSearchParams();
                formData.set("refresh_token", token);
                formData.set("grant_type", "refresh_token");
                formData.set("scope", this.authConfig.scopes.join(" "));

                return this.http.post(url, formData, {
                    headers: {
                        "content-type": "application/x-www-form-urlencoded",
                        "accept": "application/json"
                    }
                })
            }),
            map(res => res as TokenResponse),
            tap((tokens: TokenResponse) => {
                this.auth.onTokenRefresh(tokens);
            })
        );
    }

    private buildQueryUrl(url: string, params: { [key: string]: string }): string {
        const urlObj = new URL(url);
        Object.keys(params).forEach(param => {
            urlObj.searchParams.append(param, params[param]);
        });
        return urlObj.toString();
    }

    private getReshreshSecondsBefore(): number {
        return this.authConfig?.settings?.refreshTokenSecondsBefore || 10;
    }
}
