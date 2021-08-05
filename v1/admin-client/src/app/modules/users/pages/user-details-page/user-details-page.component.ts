import { Component, OnDestroy, OnInit } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { User } from "@lib";
import { UserService } from "@services";
import { ActivatedRoute, Params } from "@angular/router";
import { filter, map, startWith, switchMap, takeUntil } from "rxjs/operators";


@Component({
    selector: "app-user-details-page",
    templateUrl: "./user-details-page.component.html",
    styleUrls: ["./user-details-page.component.scss"]
})
export class UserDetailsPageComponent implements OnInit, OnDestroy {

    public user$: Observable<User>;
    private destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(private userService: UserService,
                private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.user$ = this.route.params.pipe(
            startWith(this.route.snapshot.params),
            filter((params: Params) => {
                return !!params.userId;
            }),
            map((params: Params) => {
                return params.userId as string;
            }),
            switchMap((userId: string) => {
                return this.userService.getUser(userId);
            }),
            takeUntil(this.destroy$)
        );
    }

    ngOnDestroy() {
        this.destroy$.next(true);
    }

}
