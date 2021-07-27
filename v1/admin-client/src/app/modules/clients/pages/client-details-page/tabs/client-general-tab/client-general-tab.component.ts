import { Component, Input, OnInit } from "@angular/core";
import { Client, ClientStatus, WellKnownConfig } from "src/app/models";
import { Observable } from "rxjs";
import { FormBuilder, FormGroup } from "@angular/forms";
import { startWith, switchMap, take, takeUntil } from "rxjs/operators";
import { ProviderContext } from "../../../../../../context/provider.context";

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
            name: this.fb.control(""),
            type: this.fb.control(""),
            redirectUris: this.fb.array([]),
            scopes: this.fb.array([]),
            requireConsent: this.fb.control(false)
        });


        this.wellKnown$ = this.provider.getWellKnownConfig().pipe(
            take(1)
        );
    }

}
