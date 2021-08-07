import { Component, OnDestroy, OnInit } from "@angular/core";
import { FormArray, FormBuilder, FormGroup } from "@angular/forms";
import { RolesService } from "@services";
import { Router } from "@angular/router";
import { ToastrService } from "ngx-toastr";
import { take } from "rxjs/operators";
import { Client, Role } from "@lib";

@Component({
    selector: "app-create-role-page",
    templateUrl: "./create-role-page.component.html",
    styleUrls: ["./create-role-page.component.scss"]
})
export class CreateRolePageComponent implements OnInit, OnDestroy {

    public roleForm: FormGroup;

    constructor(private roleService: RolesService,
                private fb: FormBuilder,
                private router: Router,
                private toastr: ToastrService) {
    }

    ngOnInit(): void {
        this.roleForm = this.fb.group({
            name: this.fb.control(""),
            description: this.fb.control(""),
            grantedScopes: this.fb.array([this.fb.control("")])
        });
    }

    ngOnDestroy() {

    }

    public createRole() {
        this.roleService.createRole(this.roleForm.getRawValue()).pipe(
            take(1)
        ).subscribe(() => {
            this.toastr.success("Role was created!", "Success!");
            this.router.navigate(["/roles"]);
        }, err => {
            console.error(err);
            this.toastr.error("Error creating role!", "Error!");
        });
    }

    public removeValue(formArray: FormArray, index: number): void {
        formArray.removeAt(index);
    }

    public addNewEntry(formArray: FormArray): void {
        formArray.push(this.fb.control(""));
    }

    public get scopesCtrl(): FormArray {
        return this.roleForm.controls.grantedScopes as FormArray;
    }

}
