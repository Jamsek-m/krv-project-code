import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { SettingsHomePageComponent } from "./pages/settings-home-page/settings-home-page.component";


const routes: Routes = [
    {path: "", pathMatch: "full", component: SettingsHomePageComponent}
];

@NgModule({
    imports: [
        RouterModule.forChild(routes),
    ],
    exports: [
        RouterModule,
    ]
})
export class SettingsRoutingModule {

}
