import { Component, OnDestroy, OnInit } from "@angular/core";
import { ActivatedRoute, Params } from "@angular/router";
import { ErrorService } from "@services";
import { Observable, Subject } from "rxjs";
import { PageError } from "@lib";
import { filter, map, startWith, switchMap, takeUntil } from "rxjs/operators";

@Component({
    selector: "app-error-page",
    templateUrl: "./error-page.component.html",
    styleUrls: ["./error-page.component.scss"]
})
export class ErrorPageComponent implements OnInit, OnDestroy {

    public error$: Observable<PageError>;
    private destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(private route: ActivatedRoute,
                private errorService: ErrorService) {
    }

    ngOnInit(): void {
        this.error$ = this.route.params.pipe(
            startWith(this.route.snapshot.params),
            filter((params: Params) => {
                return !!params.status;
            }),
            map((params: Params) => {
                return params.status as string;
            }),
            switchMap((status: string) => {
                return this.errorService.getError(status);
            }),
            takeUntil(this.destroy$)
        );
    }

    ngOnDestroy() {
        this.destroy$.next(true);
    }

}
