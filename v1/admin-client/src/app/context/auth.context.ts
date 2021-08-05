import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { map } from "rxjs/operators";
import { AuthState, AuthStateStatus, TokenResponse } from "@lib";
import { parseTokenPayload } from "@utils";

@Injectable({
    providedIn: "root"
})
export class AuthContext {

    private auth$: BehaviorSubject<AuthState> = new BehaviorSubject<AuthState>({
        status: AuthStateStatus.NO_TOKENS,
    });

    constructor() {

    }

    public onAuthentication(tokens: TokenResponse): void {
        const parsedToken = parseTokenPayload(tokens.access_token);
        this.auth$.next({
            status: AuthStateStatus.AUTHENTICATED,
            accessToken: tokens.access_token,
            refreshToken: tokens.refresh_token,
            idToken: tokens.id_token,
            scopes: tokens.scope.split(" "),
            parsedAccessToken: parsedToken,
            sessionState: parsedToken.sessionState,
        });
    }

    public onTokenRefresh(tokens: TokenResponse): void {
        this.onAuthentication(tokens);
    }

    public onLogout(): void {
        this.auth$.next({
            status: AuthStateStatus.NO_SESSION,
        });
    }

    public onNoSession(): void {
        this.auth$.next({
            status: AuthStateStatus.NO_SESSION,
        });
    }

    public getAuthState(): Observable<AuthState> {
        return this.auth$.asObservable();
    }

    public getRefreshToken(): Observable<string | null> {
        return this.getAuthState().pipe(
            map((state: AuthState) => {
                if (state.status === AuthStateStatus.AUTHENTICATED) {
                    return state.refreshToken;
                }
                return null;
            })
        );
    }
}
