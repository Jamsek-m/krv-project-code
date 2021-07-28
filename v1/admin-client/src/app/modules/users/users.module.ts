import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { SharedModule } from "../shared/shared.module";
import { UsersRoutingModule } from "./users-routing.module";
import { UsersListPageComponent } from './pages/users-list-page/users-list-page.component';

@NgModule({
    imports: [
        CommonModule,
        UsersRoutingModule,
        SharedModule,
    ],
    exports: [

    ],
    declarations: [
      UsersListPageComponent
    ]
})
export class UsersModule {

}
