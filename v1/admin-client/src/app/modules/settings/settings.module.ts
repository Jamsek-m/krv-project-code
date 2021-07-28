import { NgModule } from "@angular/core";
import { SettingsRoutingModule } from "./settings-routing.module";
import { CommonModule } from "@angular/common";
import { SharedModule } from "../shared/shared.module";
import { SettingsHomePageComponent } from './pages/settings-home-page/settings-home-page.component';
import { ReactiveFormsModule } from "@angular/forms";

@NgModule({
    imports: [
        CommonModule,
        SettingsRoutingModule,
        SharedModule,
        ReactiveFormsModule,
    ],
    exports: [

    ],
    declarations: [
      SettingsHomePageComponent
    ]
})
export class SettingsModule {

}
