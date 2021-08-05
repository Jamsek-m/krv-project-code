import { Component, Input, OnInit } from "@angular/core";
import { Client, ClientStatus, WellKnownConfig } from "@lib";
import { Observable } from "rxjs";
import { FormArray, FormBuilder, FormGroup } from "@angular/forms";
import { take } from "rxjs/operators";
import { ProviderContext } from "@context";

@Component({
    selector: "app-client-general-tab",
    templateUrl: "./client-general-tab.component.html",
    styleUrls: ["./client-general-tab.component.scss"]
})
export class ClientGeneralTabComponent implements OnInit {

    @Input()
    public client: Client;

    public clientStatus = ClientStatus;
    public wellKnown$: Observable<WellKnownConfig>;
    public clientForm: FormGroup;

    constructor(private fb: FormBuilder,
                private provider: ProviderContext) {
    }

    ngOnInit(): void {
        this.clientForm = this.fb.group({
            name: this.fb.control(this.client.name),
            type: this.fb.control(this.client.type),
            status: this.fb.control(this.client.status),
            redirectUris: this.fb.array(this.client.redirectUris || [this.fb.control("")]),
            webOrigins: this.fb.array(this.client.webOrigins || [this.fb.control("")]),
            scopes: this.fb.array(this.client.scopes),
            requireConsent: this.fb.control(this.client.requireConsent)
        });

        this.wellKnown$ = this.provider.getWellKnownConfig().pipe(
            take(1)
        );

        this.clientForm.valueChanges.subscribe(values => {
            console.log(values);
        })
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

    public get webOriginsCtrl(): FormArray {
        return this.clientForm.controls.webOrigins as FormArray;
    }

}
