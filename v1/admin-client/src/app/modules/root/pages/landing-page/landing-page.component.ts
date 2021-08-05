import { Component, Inject, OnDestroy, OnInit } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { map, takeUntil } from "rxjs/operators";
import { AnalyticsOverview, AuthState, AuthStateStatus } from "@lib";
import { AuthService, AnalyticsService } from "@services";
import { AUTH_CONFIG } from "@injectables";
import { AuthConfig } from "@environment/environment.types";

@Component({
    selector: "ew-landing-page",
    templateUrl: "./landing-page.component.html",
    styleUrls: ["./landing-page.component.scss"]
})
export class LandingPageComponent implements OnInit, OnDestroy {

    public authenticated$: Observable<boolean>;
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
                return state.status === AuthStateStatus.AUTHENTICATED;
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

    ngOnDestroy(): void {
        this.destroy$.next(true);
    }

}
