import { Component, OnDestroy, OnInit } from "@angular/core";
import { ActivatedRoute, Params, Router } from "@angular/router";
import { Subject } from "rxjs";
import { AuthService } from "../../../../services/auth.service";
import { filter, map, startWith, switchMap, takeUntil } from "rxjs/operators";

@Component({
    selector: "app-oidc-callback-page",
    templateUrl: "./oidc-callback-page.component.html",
    styleUrls: ["./oidc-callback-page.component.scss"]
})
export class OidcCallbackPageComponent implements OnInit, OnDestroy {

    private destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(private route: ActivatedRoute,
                private router: Router,
                private auth: AuthService) {
    }

    ngOnInit(): void {
        const code = new URLSearchParams(window.location.search).get("code");
        if (code != null) {
            this.auth.exchangeAuthorizationCode(code).subscribe(tokens => {
                console.log(tokens);
            }, err => {
                console.error(err);
            }, () => {
                this.router.navigate(["/"]);
            });
        }

        /*this.route.params.pipe(
            startWith(this.route.snapshot.params),
            filter((params: Params) => {
                console.log("params", params);
                return params && !!params.code;
            }),
            map((params: Params) => {
                return params.code;
            }),
            switchMap((code: string) => {
                return this.auth.exchangeAuthorizationCode(code);
            }),
            takeUntil(this.destroy$)
        ).subscribe(tokens => {
            console.log(tokens);
        }, err => {
            console.error(err);
        }, () => {
            this.router.navigate(["/"]);
        })*/
    }

    ngOnDestroy() {
        this.destroy$.next(true);
    }

}
