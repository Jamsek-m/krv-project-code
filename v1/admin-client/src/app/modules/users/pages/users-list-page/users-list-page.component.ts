import { Component, OnDestroy, OnInit } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { map, startWith, switchMap, takeUntil } from "rxjs/operators";
import { EntityList } from "@mjamsek/prog-utils";
import { UserService } from "@services";
import { User } from "@lib";
import { Router } from "@angular/router";

@Component({
    selector: "app-users-list-page",
    templateUrl: "./users-list-page.component.html",
    styleUrls: ["./users-list-page.component.scss"]
})
export class UsersListPageComponent implements OnInit, OnDestroy {

    private destroy$: Subject<boolean> = new Subject<boolean>();
    private reload$: Subject<void> = new Subject<void>();
    public users$: Observable<User[]>;

    constructor(private usersService: UserService,
                private router: Router) {
    }

    ngOnInit(): void {
        this.users$ = this.reload$.pipe(
            startWith(null),
            switchMap(() => {
                return this.usersService.getUsers();
            }),
            map((list: EntityList<User>) => {
                return list.entities;
            }),
            takeUntil(this.destroy$)
        );
    }

    public openUserDetails(user: User): void {
        this.router.navigate(["/users", user.id]);
    }

    ngOnDestroy() {
        this.destroy$.next(true);
    }

}
