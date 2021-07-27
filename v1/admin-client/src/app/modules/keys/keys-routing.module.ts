import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { KeysListPageComponent } from "./pages/keys-list-page/keys-list-page.component";

const routes: Routes = [
    {
        path: "", pathMatch: "full", component: KeysListPageComponent,
    }
];

@NgModule({
    imports: [
        RouterModule.forChild(routes),
    ],
    exports: [
        RouterModule,
    ]
})
export class KeysRoutingModule {

}
