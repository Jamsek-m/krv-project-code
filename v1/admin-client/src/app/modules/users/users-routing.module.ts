import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { UsersListPageComponent } from "./pages/users-list-page/users-list-page.component";
import { UserDetailsPageComponent } from "./pages/user-details-page/user-details-page.component";

const routes: Routes = [
    {path: "", pathMatch: "full", component: UsersListPageComponent},
    {path: ":userId", component: UserDetailsPageComponent}
];

@NgModule({
    imports: [
        RouterModule.forChild(routes)
    ],
    exports: [
        RouterModule
    ]
})
export class UsersRoutingModule {

}
