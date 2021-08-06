import { Component, Inject, OnDestroy, OnInit } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { map, takeUntil } from "rxjs/operators";
import { AnalyticsOverview, AuthState, AuthStateStatus } from "@lib";
import { AnalyticsService, AuthService } from "@services";
import { AUTH_CONFIG } from "@injectables";
import { AuthConfig } from "@environment/environment.types";
import { arrayIntersection } from "@utils";

type AuthDisplay = {
    authenticated: boolean;
    scopes?: string[];
}

@Component({
    selector: "az-landing-page",
    templateUrl: "./landing-page.component.html",
    styleUrls: ["./landing-page.component.scss"]
})
export class LandingPageComponent implements OnInit, OnDestroy {

    public authenticated$: Observable<AuthDisplay>;
    public analytics$: Observable<AnalyticsOverview>;
    public wellKnownUrl: string;

    private destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(private auth: AuthService,
                @Inject(AUTH_CONFIG) private authConfig: AuthConfig,
                private analyticsService: AnalyticsService) {
    }

    ngOnInit(): void {
        this.wellKnownUrl = this.authConfig.wellKnownEndpoint;

        this.authenticated$ = this.auth.getAuthState().pipe(
            map((state: AuthState) => {
                if (state.status === AuthStateStatus.AUTHENTICATED) {
                    return {
                        authenticated: true,
                        scopes: state.scopes,
                    };
                }
                return {
                    authenticated: false,
                };
            }),
            takeUntil(this.destroy$)
        );

        this.analytics$ = this.analyticsService.getOverview().pipe(
            takeUntil(this.destroy$)
        );
    }

    public login() {
        this.auth.login();
    }

    public hasPermission(state: AuthDisplay, scope: string[]): boolean {
        if (state.scopes) {
            return arrayIntersection(state.scopes, scope).length > 0;
        }
        return false;
    }

    ngOnDestroy(): void {
        this.destroy$.next(true);
    }

}
