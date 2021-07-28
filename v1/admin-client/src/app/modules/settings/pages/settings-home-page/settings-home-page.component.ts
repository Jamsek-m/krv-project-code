import { Component, OnDestroy, OnInit } from "@angular/core";
import { SettingsService } from "@services";
import { Observable, Subject } from "rxjs";
import { Settings, SettingsResponse } from "@lib";
import { map, switchMap, take, takeUntil, tap } from "rxjs/operators";
import { FormBuilder, FormControl, FormGroup } from "@angular/forms";
import { ToastrService } from "ngx-toastr";

@Component({
    selector: "app-settings-home-page",
    templateUrl: "./settings-home-page.component.html",
    styleUrls: ["./settings-home-page.component.scss"]
})
export class SettingsHomePageComponent implements OnInit, OnDestroy {

    public readonly REG_KEY = "static.config.registration.enabled";
    public settings$: Observable<SettingsResponse>;
    private destroy$: Subject<boolean> = new Subject<boolean>();

    public settingsForm: FormGroup;

    constructor(private settingsService: SettingsService,
                private toastr: ToastrService,
                private fb: FormBuilder) {
    }

    ngOnInit(): void {
        this.settingsForm = this.fb.group({
            registration: this.fb.control(false)
        });

        this.settings$ = this.settingsService.getSettings([this.REG_KEY]).pipe(
            tap((settingsMap) => {
                const registration = this.getBoolValue(settingsMap[this.REG_KEY].value);
                if (registration) {
                    this.settingsForm.patchValue({
                        registration,
                    });
                }
            }),
            takeUntil(this.destroy$)
        );

        this.registrationCtrl.valueChanges.pipe(
            switchMap((registrationValue: boolean) => {
                return this.settingsService.updateSettings(this.REG_KEY, {
                    value: `${registrationValue}`,
                }).pipe(
                    map(() => registrationValue)
                );
            }),
            takeUntil(this.destroy$)
        ).subscribe((registrationValue: boolean) => {
            const message = `Registration was ${registrationValue ? "enabled": "disabled"}!`;
            if (registrationValue) {
                this.toastr.success(message, "Success!");
            } else {
                this.toastr.warning(message, "Success!");
            }
        }, err => {
            console.error(err);
            this.toastr.error("Error updating settings!", "Error!");
        });
    }

    ngOnDestroy() {
        this.destroy$.next(true);
    }

    public getBoolValue(value: string | boolean): boolean {
        if (typeof value === "boolean") {
            return value;
        }
        return value.toUpperCase() === "TRUE";
    }

    public get registrationCtrl(): FormControl {
        return this.settingsForm.controls.registration as FormControl;
    }
}
