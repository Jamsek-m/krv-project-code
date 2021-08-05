import { Component, OnDestroy, OnInit } from "@angular/core";
import { RolesService } from "@services";
import { Observable, Subject } from "rxjs";
import { Role } from "@lib";
import { ActivatedRoute, Params, Router } from "@angular/router";
import { map, startWith, switchMap, takeUntil } from "rxjs/operators";

@Component({
    selector: "app-roles-list-page",
    templateUrl: "./roles-list-page.component.html",
    styleUrls: ["./roles-list-page.component.scss"]
})
export class RolesListPageComponent implements OnInit, OnDestroy {

    public roles$: Observable<Role[]>;
    private destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(private roleService: RolesService,
                private route: ActivatedRoute,
                private router: Router) {
    }

    ngOnInit(): void {
        this.roles$ = this.route.params.pipe(
            startWith(this.route.snapshot.params),
            switchMap(() => {
                return this.roleService.getRoles();
            }),
            takeUntil(this.destroy$)
        );
    }

    ngOnDestroy(): void {
        this.destroy$.next(true);
    }

    public openRole(role: Role): void {
        this.router.navigate(["/roles", role.id]);
    }

    public getRoleIdentifier(index: number, role: Role): string {
        return role.id;
    }

}
