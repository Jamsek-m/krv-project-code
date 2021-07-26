import { Component, OnDestroy, OnInit } from "@angular/core";
import { ClientService } from "../../../../services";
import { Observable, Subject } from "rxjs";
import { Client } from "../../../../models";
import { map, takeUntil } from "rxjs/operators";
import { EntityList } from "@mjamsek/prog-utils";

@Component({
    selector: "app-clients-list-page",
    templateUrl: "./clients-list-page.component.html",
    styleUrls: ["./clients-list-page.component.scss"]
})
export class ClientsListPageComponent implements OnInit, OnDestroy {

    private destroy$ = new Subject<boolean>();
    public clients$: Observable<Client[]>;

    constructor(private clientService: ClientService) {
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

}
