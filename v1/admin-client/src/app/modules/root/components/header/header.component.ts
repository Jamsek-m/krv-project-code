import { Component, OnDestroy, OnInit } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { map, takeUntil } from "rxjs/operators";
import { menuItems } from "../../../../config/menu.config";
import { NavbarContext } from "@context";
import { AuthState, AuthStateStatus, MenuItem } from "@lib";
import { AuthService } from "@services";
import { arrayIntersection } from "@utils";

@Component({
    selector: "az-header",
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

                if (state.status === AuthStateStatus.AUTHENTICATED) {
                    return menuItems.filter(item => {
                        // If item requires auth, check if user has permissions
                        if (item.requireAuth) {
                            // If item has required scopes, check if user has those, otherwise, hide it
                            if (item.requiredScopes) {
                                const matchingScopes = arrayIntersection(state.scopes, item.requiredScopes);
                                return matchingScopes.length > 0;
                            }
                            // No required scopes defined
                            return true;
                        } else {
                            // Allow all items, not requiring authentication
                            return true;
                        }
                    });
                }

                // If user is not authenticated, return only items not requiring authentication
                return menuItems.filter(item => {
                    return !item.requireAuth;
                });
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
