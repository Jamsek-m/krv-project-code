import { Component, OnDestroy, OnInit } from "@angular/core";
import { ClientService } from "../../../../services";
import { Observable, Subject } from "rxjs";
import { Client, WellKnownConfig } from "../../../../models";
import { map, takeUntil } from "rxjs/operators";
import { EntityList } from "@mjamsek/prog-utils";
import { Router } from "@angular/router";
import { ProviderContext } from "../../../../context/provider.context";

@Component({
    selector: "app-clients-list-page",
    templateUrl: "./clients-list-page.component.html",
    styleUrls: ["./clients-list-page.component.scss"]
})
export class ClientsListPageComponent implements OnInit, OnDestroy {

    private destroy$ = new Subject<boolean>();
    public clients$: Observable<Client[]>;

    constructor(private clientService: ClientService,
                private provider: ProviderContext,
                private router: Router) {
    }

    ngOnInit(): void {
        this.clients$ = this.clientService.getClients().pipe(
            map((list: EntityList<Client>) => {
                return list.entities;
            }),
            takeUntil(this.destroy$)
        );
    }

    ngOnDestroy(): void {
        this.destroy$.next(true);
    }

    public addClient() {
        this.router.navigate(["/clients/add"]);
    }

    public getClientIdentifier(index: number, client: Client): string {
        return client.id;
    }

}
