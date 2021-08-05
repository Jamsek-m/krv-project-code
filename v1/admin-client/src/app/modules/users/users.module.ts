import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { SharedModule } from "../shared/shared.module";
import { UsersRoutingModule } from "./users-routing.module";
import { UsersListPageComponent } from "./pages/users-list-page/users-list-page.component";
import { UserDetailsPageComponent } from "./pages/user-details-page/user-details-page.component";
import { UserGeneralTabComponent } from './pages/user-details-page/tabs/user-general-tab/user-general-tab.component';
import { UserRolesTabComponent } from './pages/user-details-page/tabs/user-roles-tab/user-roles-tab.component';
import { FormsModule, ReactiveFormsModule } from "@angular/forms";

@NgModule({
    imports: [
        CommonModule,
        UsersRoutingModule,
        SharedModule,
        FormsModule,
        ReactiveFormsModule,
    ],
    exports: [],
    declarations: [
        UsersListPageComponent,
        UserDetailsPageComponent,
        UserGeneralTabComponent,
        UserRolesTabComponent
    ]
})
export class UsersModule {

}
