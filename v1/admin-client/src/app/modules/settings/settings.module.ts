import { NgModule } from "@angular/core";
import { SettingsRoutingModule } from "./settings-routing.module";
import { CommonModule } from "@angular/common";
import { SharedModule } from "../shared/shared.module";
import { SettingsHomePageComponent } from './pages/settings-home-page/settings-home-page.component';

@NgModule({
    imports: [
        CommonModule,
        SettingsRoutingModule,
        SharedModule,
    ],
    exports: [

    ],
    declarations: [
      SettingsHomePageComponent
    ]
})
export class SettingsModule {

}
