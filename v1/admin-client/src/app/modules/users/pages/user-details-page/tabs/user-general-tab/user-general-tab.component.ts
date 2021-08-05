import { Component, Input, OnInit } from "@angular/core";
import { User, UserAttribute } from "@lib";
import { FormArray, FormBuilder, FormControl, FormGroup } from "@angular/forms";
import { UserService } from "@services";
import { take } from "rxjs/operators";

@Component({
    selector: "app-user-general-tab",
    templateUrl: "./user-general-tab.component.html",
    styleUrls: ["./user-general-tab.component.scss"]
})
export class UserGeneralTabComponent implements OnInit {

    @Input()
    public user: User;

    public userForm: FormGroup;

    constructor(private fb: FormBuilder,
                private userService: UserService) {
    }

    ngOnInit(): void {
        this.userForm = this.fb.group({
            firstName: this.fb.control(this.user.firstName),
            lastName: this.fb.control(this.user.lastName),
            attributes: this.fb.array(this.user.attributes.map(attr => this.attributeToFormGroup(this.fb, attr)))
        });
    }

    private attributeToFormGroup(fb: FormBuilder, attr: UserAttribute): FormGroup {
        return fb.group({
            value: fb.control(attr.value),
            key: fb.control(attr.key),
            type: fb.control(attr.type),
        });
    }

    public saveUserInfo(): void {
        const req: Partial<User> = {
            firstName: this.nameCtrl("firstName").value,
            lastName: this.nameCtrl("lastName").value,
        };
        this.userService.patchUser(this.user.id, req).pipe(
            take(1)
        ).subscribe(() => {

        });
    }

    public get attributesCtrl(): FormArray {
        return this.userForm.controls.attributes as FormArray;
    }

    public nameCtrl(nameId: string): FormControl {
        return this.userForm.controls[nameId] as FormControl;
    }

    public removeEntry(index: number): void {
        this.attributesCtrl.removeAt(index);
    }

    public addNewEntry(): void {
        this.attributesCtrl.push(this.fb.group({
            key: this.fb.control(""),
            value: this.fb.control(""),
            type: this.fb.control("STRING")
        }));
    }

}
