import {
    AfterViewInit,
    Component,
    ElementRef,
    HostListener,
    Inject, OnDestroy,
    OnInit,
    SecurityContext,
    ViewChild
} from "@angular/core";
import { DomSanitizer } from "@angular/platform-browser";
import { map, switchMap, take, takeUntil, tap } from "rxjs/operators";
import { ProviderContext } from "@context";
import { AuthStateStatus, WellKnownConfig } from "@lib";
import { AuthConfig } from "@environment/environment.types";
import { AUTH_CONFIG } from "@injectables";
import { combineLatest, forkJoin, interval, Subject, timer } from "rxjs";
import { AuthService } from "@services";

type SessionCheckStatus = "unchanged" | "changed" | "error";

@Component({
    selector: "app-session-check",
    styles: [
        `iframe {
            display: none;
        }`
    ],
    template: `
        <iframe #checkSSOIframe id="authz-sso-iframe"></iframe>
    `
})
export class SessionCheckComponent implements OnInit, AfterViewInit, OnDestroy {

    @ViewChild("checkSSOIframe", {static: false})
    private checkSSOContainer: ElementRef<HTMLIFrameElement>;

    private issuer: string;

    private sessionCheckInterval = 5000;

    private destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(@Inject(AUTH_CONFIG) private authConfig: AuthConfig,
                private provider: ProviderContext,
                private auth: AuthService,
                private sanitizer: DomSanitizer) {
    }

    ngOnInit(): void {
        this.auth.silentLogin();

        if (this.authConfig.settings && this.authConfig.settings.checkSessionEverySeconds) {
            this.sessionCheckInterval = this.authConfig.settings.checkSessionEverySeconds * 1000;
        }
    }

    ngAfterViewInit() {
        this.provider.getWellKnownConfig().pipe(
            take(1),
        ).subscribe((config: WellKnownConfig) => {
            this.checkSSOContainer.nativeElement.src = this.sanitizer.sanitize(SecurityContext.URL, config.check_session_iframe)!;
            this.issuer = config.issuer;
        });

        timer(0, this.sessionCheckInterval).pipe(
            switchMap(() => {
                return combineLatest([
                    this.auth.getAuthState(),
                    this.provider.getWellKnownConfig(),
                ]);
            }),
            takeUntil(this.destroy$)
        ).subscribe((result) => {
            const [auth, wellKnown] = result;
            if (auth.status === AuthStateStatus.AUTHENTICATED) {
                this.checkSession(wellKnown.issuer, auth.sessionState);
            } else {
                this.checkSession(wellKnown.issuer);
            }
        });
    }

    private checkSession(providerOrigin: string, sessionState?: string): void {
        const message = `${this.authConfig.clientId} ${sessionState || ""}`;
        const ssoWindow = this.checkSSOContainer.nativeElement.contentWindow;
        if (ssoWindow !== null) {
            try {
                ssoWindow.postMessage(message, providerOrigin);
            } catch (ignored) {

            }
        } else {
            console.warn("Unable to instantiate SSO iFrame window!");
        }
    }

    @HostListener("window:message", ["$event"])
    private onIframeMessage($event: MessageEvent): void {
        // Accept only issuer origin
        if ($event.origin !== this.issuer) {
            return;
        }

        // Accept only string data
        if (typeof $event.data !== "string") {
            return;
        }

        const status: SessionCheckStatus = $event.data as SessionCheckStatus;
        if (status === "changed") {
            console.log("session status changed! Need to reauthenticate");
            // this.auth.silentLogin();
        } else if (status === "unchanged") {
            console.log("session status: ", status);
        } else {
            console.warn("Unable to retrieve session information!");
        }

    }

    ngOnDestroy() {
        this.destroy$.next(true);
    }

}
