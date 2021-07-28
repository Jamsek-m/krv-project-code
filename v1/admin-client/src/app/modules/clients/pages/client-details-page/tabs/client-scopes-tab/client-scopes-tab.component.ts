import { Component, Input, OnInit } from "@angular/core";
import { Client } from "../../../../../../models";
import { FormArray, FormBuilder, FormGroup } from "@angular/forms";
import { ClientService } from "../../../../../../services";
import { takeUntil } from "rxjs/operators";
import { Subject } from "rxjs";
import { ToastrService } from "ngx-toastr";

@Component({
    selector: "app-client-scopes-tab",
    templateUrl: "./client-scopes-tab.component.html",
    styleUrls: ["./client-scopes-tab.component.scss"]
})
export class ClientScopesTabComponent implements OnInit {

    @Input()
    public client: Client;

    public scopeForm: FormGroup;
    private destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(private clientService: ClientService,
                private toastr: ToastrService,
                private fb: FormBuilder) {
    }

    ngOnInit(): void {
        this.scopeForm = this.fb.group({
            scopes: this.fb.array(this.client.scopes || [this.fb.control("")])
        });
    }

    public onFormSubmit() {
        this.clientService.patchClient(this.client.id, this.scopeForm.getRawValue()).pipe(
            takeUntil(this.destroy$)
        ).subscribe(() => {
            this.toastr.success("Client scopes updated!", "Success!");
        }, (err) => {
            console.error(err);
            this.toastr.error("Error updating client!", "Error!");
        });
    }

    public removeValue(index: number): void {
        this.scopesCtrl.removeAt(index);
    }

    public addNewEntry(): void {
        this.scopesCtrl.push(this.fb.control(""));
    }

    public get scopesCtrl(): FormArray {
        return this.scopeForm.controls.scopes as FormArray;
    }

}
