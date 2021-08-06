import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from "@angular/core";
import { Client, ClientStatus, WellKnownConfig } from "@lib";
import { Observable, Subject } from "rxjs";
import { FormArray, FormBuilder, FormGroup } from "@angular/forms";
import { debounceTime, map, switchMap, take, takeUntil } from "rxjs/operators";
import { ProviderContext } from "@context";
import { ClientService } from "@services";
import { ToastrService } from "ngx-toastr";

@Component({
    selector: "app-client-general-tab",
    templateUrl: "./client-general-tab.component.html",
    styleUrls: ["./client-general-tab.component.scss"]
})
export class ClientGeneralTabComponent implements OnInit, OnDestroy {

    @Input()
    public client: Client;

    @Output()
    public whenChanged: EventEmitter<void> = new EventEmitter<void>();

    public clientStatus = ClientStatus;
    public wellKnown$: Observable<WellKnownConfig>;
    public clientForm: FormGroup;
    public statusForm: FormGroup;
    private destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(private fb: FormBuilder,
                private provider: ProviderContext,
                private clientService: ClientService,
                private toastr: ToastrService) {
    }

    ngOnInit(): void {
        this.clientForm = this.fb.group({
            name: this.fb.control(this.client.name),
            type: this.fb.control(this.client.type),
            redirectUris: this.fb.array(this.client.redirectUris || [this.fb.control("")]),
            webOrigins: this.fb.array(this.client.webOrigins || [this.fb.control("")]),
            scopes: this.fb.array(this.client.scopes),
            requireConsent: this.fb.control(this.client.requireConsent)
        });
        this.statusForm = this.fb.group({
            status: this.fb.control(this.client.status),
        });

        this.wellKnown$ = this.provider.getWellKnownConfig().pipe(
            take(1)
        );

        this.clientForm.valueChanges.pipe(
            debounceTime(300),
            switchMap((values) => {
                return this.clientService.patchClient(this.client.id, values);
            }),
            takeUntil(this.destroy$)
        ).subscribe(() => {
            this.whenChanged.next();
            this.toastr.success("Client updated!", "Success!");
        }, err => {
            console.error(err);
            this.toastr.error("Error updating client!", "Error!");
        });

        this.statusForm.valueChanges.pipe(
            switchMap((values) => {
                const {status} = values;
                return this.clientService.changeClientStatus(this.client.clientId, status).pipe(
                    map(() => status)
                );
            }),
            takeUntil(this.destroy$)
        ).subscribe((status: boolean) => {
            this.whenChanged.next();
            this.toastr.success(`Client ${status ? "enabled" : "disabled"}!`, "Success!");
        }, err => {
            console.error(err);
            this.toastr.error("Error updating client!", "Error!");
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

    public get webOriginsCtrl(): FormArray {
        return this.clientForm.controls.webOrigins as FormArray;
    }

    ngOnDestroy() {
        this.destroy$.next(true);
    }

}
