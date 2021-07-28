import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { Client, WellKnownConfig } from "../../../../../../models";
import { Observable, Subject } from "rxjs";
import { ProviderContext } from "../../../../../../context/provider.context";
import { switchMap, take, takeUntil } from "rxjs/operators";
import { FormBuilder, FormGroup } from "@angular/forms";
import { ClientService } from "../../../../../../services";
import { ToastrService } from "ngx-toastr";
import { HttpErrorResponse } from "@angular/common/http";

@Component({
    selector: "app-client-signing-tab",
    templateUrl: "./client-signing-tab.component.html",
    styleUrls: ["./client-signing-tab.component.scss"]
})
export class ClientSigningTabComponent implements OnInit {

    @Input()
    public client: Client;

    @Output()
    public whenChanged: EventEmitter<void> = new EventEmitter<void>();

    public wellKnown$: Observable<WellKnownConfig>;
    public algForm: FormGroup;
    private destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(private provider: ProviderContext,
                private clientService: ClientService,
                private toastr: ToastrService,
                private fb: FormBuilder) {
    }

    ngOnInit(): void {
        this.wellKnown$ = this.provider.getWellKnownConfig().pipe(
            take(1)
        );

        this.algForm = this.fb.group({
            signingKeyAlgorithm: this.fb.control(this.client.signingKeyAlgorithm),
            pkceMethod: this.fb.control(this.client.pkceMethod),
        });

        this.algForm.valueChanges.pipe(
            switchMap((formData: Partial<Client>) => {
                return this.clientService.patchClient(this.client.id, formData);
            }),
            takeUntil(this.destroy$)
        ).subscribe(() => {
            this.toastr.success("Client updated!", "Success!");
        }, (err: HttpErrorResponse) => {
            console.error(err);
            this.toastr.error("Error updating client!", "Error!");
        });
    }

    public regenerateSecret() {
        this.clientService.regenerateClientSecret(this.client.id).pipe(
            take(1)
        ).subscribe(() => {
            this.whenChanged.next();
            this.toastr.success("Secret created!", "Success!");
        }, err => {
            console.error(err);
            this.toastr.error("Error updating client!", "Error!");
        })
    }

}
