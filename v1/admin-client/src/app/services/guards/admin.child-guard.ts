import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivateChild, Router, RouterStateSnapshot, UrlTree } from "@angular/router";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { AuthState, AuthStateStatus } from "@lib";
import { AuthService } from "@services";

@Injectable({
    providedIn: "root"
})
export class AdminChildGuard implements CanActivateChild {

    constructor(private auth: AuthService,
                private router: Router) {
    }

    canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
        return this.auth.getAuthState().pipe(
            map((state: AuthState) => {
                if (state.status === AuthStateStatus.AUTHENTICATED) {
                    if (state.scopes.includes("admin")) {
                        return true;
                    }
                    this.router.navigate(["/error/403"]);
                    return false;
                } else {
                    this.router.navigate(["/"]);
                    return false;
                }
            })
        );
    }

}
