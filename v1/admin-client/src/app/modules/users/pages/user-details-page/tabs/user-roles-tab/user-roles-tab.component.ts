import { Component, Input, OnDestroy, OnInit } from "@angular/core";
import { Role, User, AggregatedRoles } from "@lib";
import { RolesService } from "@services";
import { Observable, Subject } from "rxjs";
import { startWith, switchMap, take, takeUntil } from "rxjs/operators";

@Component({
    selector: "app-user-roles-tab",
    templateUrl: "./user-roles-tab.component.html",
    styleUrls: ["./user-roles-tab.component.scss"]
})
export class UserRolesTabComponent implements OnInit, OnDestroy {

    @Input()
    public user: User;

    public roles$: Observable<AggregatedRoles>;
    private reload$: Subject<void> = new Subject<void>();
    private destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(private roleService: RolesService) {
    }

    ngOnInit(): void {
        this.roles$ = this.reload$.pipe(
            startWith(null),
            switchMap(() => {
                return this.roleService.getAggregatedUserRoles(this.user.id);
            }),
            takeUntil(this.destroy$)
        );
    }

    ngOnDestroy() {
        this.destroy$.next(true);
    }

    public assignRoleToUser(role: Role): void {
        this.roleService.assignRoleToUser(this.user.id, role.id).pipe(
            take(1),
        ).subscribe(() => {
            this.reload$.next();
        });
    }

    public unassignRole(role: Role): void {
        this.roleService.unassignRoleToUser(this.user.id, role.id).pipe(
            take(1),
        ).subscribe(() => {
            this.reload$.next();
        });
    }

    public getRoleIdentifier(index: number, role: Role): string {
        return role.id;
    }

}
