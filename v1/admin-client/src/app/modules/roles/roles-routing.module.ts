import { RouterModule, Routes } from "@angular/router";
import { NgModule } from "@angular/core";
import { RolesListPageComponent } from "./pages/roles-list-page/roles-list-page.component";
import { RoleDetailsPageComponent } from "./pages/role-details-page/role-details-page.component";


const routes: Routes = [
    {path: "", pathMatch: "full", component: RolesListPageComponent},
    {path: ":roleId", component: RoleDetailsPageComponent}
];

@NgModule({
    imports: [
        RouterModule.forChild(routes)
    ],
    exports: [
        RouterModule
    ]
})
export class RolesRoutingModule {

}
