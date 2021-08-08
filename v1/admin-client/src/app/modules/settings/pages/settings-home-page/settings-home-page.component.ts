import { Component, OnDestroy, OnInit } from "@angular/core";
import { SettingsService } from "@services";
import { Observable, Subject } from "rxjs";
import { SettingsResponse } from "@lib";
import { takeUntil } from "rxjs/operators";
import { REGISTRATION_KEY } from "../../../../config/settings.config";

@Component({
    selector: "app-settings-home-page",
    templateUrl: "./settings-home-page.component.html",
    styleUrls: ["./settings-home-page.component.scss"]
})
export class SettingsHomePageComponent implements OnInit, OnDestroy {

    public readonly REG_KEY = REGISTRATION_KEY;
    public settings$: Observable<SettingsResponse>;
    private destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(private settingsService: SettingsService) {
    }

    ngOnInit(): void {
        this.settings$ = this.settingsService.getSettings([REGISTRATION_KEY]).pipe(
            takeUntil(this.destroy$)
        );
    }

    ngOnDestroy() {
        this.destroy$.next(true);
    }
}
