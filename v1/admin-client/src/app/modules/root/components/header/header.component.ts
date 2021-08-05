import { Component, OnDestroy, OnInit } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { map, takeUntil } from "rxjs/operators";
import { menuItems } from "../../../../config/menu.config";
import { NavbarContext } from "@context";
import { AuthState, AuthStateStatus, MenuItem } from "@lib";
import { AuthService } from "@services";

@Component({
    selector: "ew-header",
    templateUrl: "./header.component.html",
    styleUrls: ["./header.component.scss"]
})
export class HeaderComponent implements OnInit, OnDestroy {

    public authStates = AuthStateStatus;

    public menu$: Observable<MenuItem[]>;
    public auth$: Observable<AuthState>;

    private destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(public nav: NavbarContext,
                private auth: AuthService) {

    }

    ngOnInit(): void {
        this.auth$ = this.auth.getAuthState().pipe(
            takeUntil(this.destroy$)
        );

        this.menu$ = this.auth.getAuthState().pipe(
            map((state: AuthState) => {
                const authenticated = state.status === AuthStateStatus.AUTHENTICATED;
                return menuItems.filter(item => {
                    if (item.requireAuth && authenticated) {
                        return true;
                    } else if (!item.requireAuth) {
                        return true;
                    }
                    return false;
                })
            }),
            takeUntil(this.destroy$)
        );
    }

    public logout(): void {
        this.auth.logout();
    }

    public login(): void {
        this.auth.login();
    }

    ngOnDestroy(): void {
        this.destroy$.next(true);
    }

}
