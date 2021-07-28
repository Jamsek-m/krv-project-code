import { Component, OnDestroy, OnInit } from "@angular/core";
import { UserService } from "../../../../services";
import { Observable, Subject } from "rxjs";
import { User } from "../../../../models";
import { map, startWith, switchMap, takeUntil } from "rxjs/operators";
import { EntityList } from "@mjamsek/prog-utils";

@Component({
    selector: "app-users-list-page",
    templateUrl: "./users-list-page.component.html",
    styleUrls: ["./users-list-page.component.scss"]
})
export class UsersListPageComponent implements OnInit, OnDestroy {

    private destroy$: Subject<boolean> = new Subject<boolean>();
    private reload$: Subject<void> = new Subject<void>();
    public users$: Observable<User[]>;

    constructor(private usersService: UserService) {
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

    ngOnDestroy() {
        this.destroy$.next(true);
    }

}
