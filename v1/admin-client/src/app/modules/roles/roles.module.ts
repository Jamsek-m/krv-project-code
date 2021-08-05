import { NgModule } from "@angular/core";
import { RouterModule } from "@angular/router";
import { CommonModule } from "@angular/common";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { SharedModule } from "../shared/shared.module";
import { RolesRoutingModule } from "./roles-routing.module";
import { RolesListPageComponent } from './pages/roles-list-page/roles-list-page.component';
import { RoleDetailsPageComponent } from './pages/role-details-page/role-details-page.component';


@NgModule({
    imports: [
        CommonModule,
        RolesRoutingModule,
        FormsModule,
        ReactiveFormsModule,
        SharedModule,
    ],
    exports: [
        RouterModule
    ],
    declarations: [
      RolesListPageComponent,
      RoleDetailsPageComponent
    ]
})
export class RolesModule {

}
