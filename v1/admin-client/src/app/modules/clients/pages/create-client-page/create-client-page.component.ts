import { Component, OnInit } from "@angular/core";
import { ClientService } from "@services";
import { FormArray, FormBuilder, FormGroup } from "@angular/forms";
import { take } from "rxjs/operators";
import { ToastrService } from "ngx-toastr";
import { Client } from "@lib";
import { Router } from "@angular/router";

@Component({
    selector: "app-create-client-page",
    templateUrl: "./create-client-page.component.html",
    styleUrls: ["./create-client-page.component.scss"]
})
export class CreateClientPageComponent implements OnInit {

    public clientForm: FormGroup;

    constructor(private clientService: ClientService,
                private fb: FormBuilder,
                private router: Router,
                private toastr: ToastrService) {
    }

    ngOnInit(): void {
        this.clientForm = this.fb.group({
            clientId: this.fb.control(""),
            name: this.fb.control(""),
            type: this.fb.control("PUBLIC"),
            redirectUris: this.fb.array([this.fb.control("")]),
        });
    }

    public createClient(): void {
        this.clientService.createClient(this.clientForm.getRawValue()).pipe(
            take(1)
        ).subscribe((createdClient: Client) => {
            this.toastr.success("Client was created!", "Success!");
            this.router.navigate(["/clients", createdClient.clientId]);
        }, err => {
            console.error(err);
            this.toastr.error("Error creating client!", "Error!");
        });
    }

    public removeValue(formArray: FormArray, index: number): void {
        formArray.removeAt(index);
    }

    public addNewEntry(formArray: FormArray): void {
        formArray.push(this.fb.control(""));
    }

    public get redirectUrisCtrl(): FormArray {
        return this.clientForm.controls.redirectUris as FormArray;
    }

}
