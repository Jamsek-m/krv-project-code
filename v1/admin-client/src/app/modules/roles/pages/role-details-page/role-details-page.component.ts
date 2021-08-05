import { Component, OnDestroy, OnInit } from "@angular/core";
import { RolesService } from "@services";
import { ActivatedRoute, Params } from "@angular/router";
import { Observable, Subject } from "rxjs";
import { Role } from "@lib";
import { filter, map, startWith, switchMap, take, takeUntil, tap } from "rxjs/operators";
import { FormArray, FormBuilder, FormGroup } from "@angular/forms";
import { ToastrService } from "ngx-toastr";

@Component({
    selector: "app-role-details-page",
    templateUrl: "./role-details-page.component.html",
    styleUrls: ["./role-details-page.component.scss"]
})
export class RoleDetailsPageComponent implements OnInit, OnDestroy {

    public scopesForm: FormGroup;
    public role$: Observable<Role>;

    private destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(private roleService: RolesService,
                private route: ActivatedRoute,
                private fb: FormBuilder,
                private toastr: ToastrService) {
    }

    ngOnInit(): void {
        this.scopesForm = this.fb.group({
            scopes: this.fb.array([])
        });

        this.role$ = this.route.params.pipe(
            startWith(this.route.snapshot.params),
            filter((params: Params) => {
                return !!params.roleId;
            }),
            map((params: Params) => {
                return params.roleId as string;
            }),
            switchMap((roleId: string) => {
                return this.roleService.getRole(roleId);
            }),
            tap((role: Role) => {
                setTimeout(() => {
                    this.scopesCtrl.clear();
                    role.grantedScopes.forEach(grantedScope => {
                        this.scopesCtrl.push(this.fb.control(grantedScope));
                    });
                }, 0);
            }),
            takeUntil(this.destroy$)
        );
    }

    ngOnDestroy() {
        this.destroy$.next(true);
    }

    public saveScopeChange(roleId: string): void {
        const req: Partial<Role> = {
            grantedScopes: this.scopesCtrl.getRawValue(),
        }
        this.roleService.patchRole(roleId, req).pipe(
            take(1),
        ).subscribe(() => {
            this.toastr.success("Scopes updated!", "Success!");
        }, err => {
            console.error(err);
            this.toastr.error("Error updating scopes!", "Error!");
        });
    }

    public removeScope(index: number): void {
        this.scopesCtrl.removeAt(index);
    }

    public addScope(): void {
        this.scopesCtrl.push(this.fb.control(""));
    }

    public get scopesCtrl(): FormArray {
        return this.scopesForm.controls.scopes as FormArray;
    }
}
