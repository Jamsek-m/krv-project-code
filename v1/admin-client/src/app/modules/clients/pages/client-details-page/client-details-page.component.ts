import { Component, OnDestroy, OnInit } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { Client } from "../../../../models";
import { ActivatedRoute } from "@angular/router";
import { startWith, switchMap, takeUntil } from "rxjs/operators";
import { ClientService } from "../../../../services";
import { FormBuilder, FormControl, FormGroup } from "@angular/forms";

@Component({
    selector: "app-client-details-page",
    templateUrl: "./client-details-page.component.html",
    styleUrls: ["./client-details-page.component.scss"]
})
export class ClientDetailsPageComponent implements OnInit, OnDestroy {

    private destroy$ = new Subject<boolean>();
    private identifier$ = new Subject<string>();

    public client$: Observable<Client>;
    public clientForm: FormGroup;

    constructor(private route: ActivatedRoute,
                private clientService: ClientService,
                private fb: FormBuilder) {
    }

    ngOnInit(): void {
        this.clientForm = this.fb.group({
            name: this.fb.control(""),
            type: this.fb.control(""),
            redirectUris: this.fb.array([]),
            scopes: this.fb.array([]),
            requireConsent: this.fb.control(false)
        });

        this.client$ = this.identifier$.pipe(
            startWith(this.route.snapshot.params.clientId),
            switchMap((clientId: string) => {
                return this.clientService.getClient(clientId);
            }),
            takeUntil(this.destroy$)
        );
    }

    public get nameCtrl(): FormControl {
        return this.clientForm.controls.name as FormControl;
    }

    ngOnDestroy(): void {
        this.destroy$.next(true);
    }

}
