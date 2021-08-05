import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { take } from "rxjs/operators";
import { AuthService } from "@services";

@Component({
    selector: "app-oidc-callback-page",
    template: ``
})
export class OidcCallbackPageComponent implements OnInit {

    constructor(private route: ActivatedRoute,
                private router: Router,
                private auth: AuthService) {
    }

    ngOnInit(): void {
        const queryParams = new URLSearchParams(window.location.search);
        const code = queryParams.get("code");
        const error = queryParams.get("error");
        if (code != null) {
            this.auth.exchangeAuthorizationCode(code).pipe(take(1)).subscribe(tokens => {
                // console.log(tokens);
            }, err => {
                console.error("Error retrieving authorization code!", err);
            }, () => {
                this.router.navigate(["/"]);
            });
        } else if (error != null) {
            if (error === "login_required") {
                this.auth.onNoSessionError();
            } else {
                console.warn("OIDC error: ", error);
            }
            this.router.navigate(["/"]);
        } else {
            this.router.navigate(["/"]);
        }
    }

}
