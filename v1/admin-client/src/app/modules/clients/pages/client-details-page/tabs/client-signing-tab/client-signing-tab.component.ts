import { Component, Input, OnInit } from "@angular/core";
import { Client, WellKnownConfig } from "../../../../../../models";
import { Observable } from "rxjs";
import { ProviderContext } from "../../../../../../context/provider.context";
import { take } from "rxjs/operators";

@Component({
    selector: "app-client-signing-tab",
    templateUrl: "./client-signing-tab.component.html",
    styleUrls: ["./client-signing-tab.component.scss"]
})
export class ClientSigningTabComponent implements OnInit {

    @Input()
    public client: Client;

    public wellKnown$: Observable<WellKnownConfig>;

    constructor(private provider: ProviderContext) {
    }

    ngOnInit(): void {
        this.wellKnown$ = this.provider.getWellKnownConfig().pipe(
            take(1)
        );
    }

}
