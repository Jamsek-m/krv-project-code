import { Component, Input, OnDestroy, OnInit } from "@angular/core";
import { FormBuilder, FormControl, FormGroup } from "@angular/forms";
import { Subject } from "rxjs";
import { map, switchMap, takeUntil } from "rxjs/operators";
import { ToastrService } from "ngx-toastr";
import { SettingsService } from "@services";
import { Settings } from "@lib";
import { REGISTRATION_KEY } from "../../../../config/settings.config";

@Component({
    selector: "app-registration-settings-form",
    templateUrl: "./registration-settings-form.component.html",
    styleUrls: ["./registration-settings-form.component.scss"]
})
export class RegistrationSettingsFormComponent implements OnInit, OnDestroy {

    @Input()
    public registration: Settings;

    public settingsForm: FormGroup;

    private destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(private fb: FormBuilder,
                private settingsService: SettingsService,
                private toastr: ToastrService) {
    }

    ngOnInit(): void {
        this.settingsForm = this.fb.group({
            registration: this.fb.control(Settings.getBooleanValue(this.registration))
        });

        this.registrationCtrl.valueChanges.pipe(
            switchMap((registrationValue: boolean) => {
                return this.settingsService.updateSettings(REGISTRATION_KEY, {
                    value: `${registrationValue}`,
                }).pipe(
                    map(() => registrationValue)
                );
            }),
            takeUntil(this.destroy$)
        ).subscribe((registrationValue: boolean) => {
            const message = `Registration was ${registrationValue ? "enabled" : "disabled"}!`;
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

    public get registrationCtrl(): FormControl {
        return this.settingsForm.controls.registration as FormControl;
    }

    ngOnDestroy() {
        this.destroy$.next(true);
    }

}
