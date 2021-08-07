import { RouterModule, Routes } from "@angular/router";
import { NgModule } from "@angular/core";
import { RolesListPageComponent } from "./pages/roles-list-page/roles-list-page.component";
import { RoleDetailsPageComponent } from "./pages/role-details-page/role-details-page.component";
import { CreateRolePageComponent } from "./pages/create-role-page/create-role-page.component";


const routes: Routes = [
    {path: "", pathMatch: "full", component: RolesListPageComponent},
    {path: "new", pathMatch: "full", component: CreateRolePageComponent},
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
