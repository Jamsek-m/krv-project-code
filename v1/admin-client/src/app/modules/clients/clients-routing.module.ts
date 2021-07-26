import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { ClientsListPageComponent } from "./pages/clients-list-page/clients-list-page.component";
import { ClientDetailsPageComponent } from "./pages/client-details-page/client-details-page.component";

const routes: Routes = [
    {path: "", pathMatch: "full", component: ClientsListPageComponent},
    {path: ":clientId", component: ClientDetailsPageComponent}
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class ClientsRoutingModule {
}
