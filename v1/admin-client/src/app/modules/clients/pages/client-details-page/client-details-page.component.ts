import { Component, OnDestroy, OnInit } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { Client } from "../../../../models";
import { ActivatedRoute } from "@angular/router";
import { startWith, switchMap, takeUntil } from "rxjs/operators";
import { ClientService } from "../../../../services";
import { ToastrService } from "ngx-toastr";

@Component({
    selector: "app-client-details-page",
    templateUrl: "./client-details-page.component.html",
    styleUrls: ["./client-details-page.component.scss"]
})
export class ClientDetailsPageComponent implements OnInit, OnDestroy {

    private destroy$ = new Subject<boolean>();
    private identifier$ = new Subject<string>();

    public client$: Observable<Client>;

    constructor(private route: ActivatedRoute,
                private clientService: ClientService,
                private toastr: ToastrService) {
    }

    ngOnInit(): void {
        this.client$ = this.identifier$.pipe(
            startWith(this.route.snapshot.params.clientId),
            switchMap((clientId: string) => {
                return this.clientService.getClient(clientId);
            }),
            takeUntil(this.destroy$)
        );
    }

    ngOnDestroy(): void {
        this.destroy$.next(true);
    }

}
