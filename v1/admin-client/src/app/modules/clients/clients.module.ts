import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";

import { ClientsRoutingModule } from "./clients-routing.module";
import { ClientsListPageComponent } from "./pages/clients-list-page/clients-list-page.component";
import { ClientDetailsPageComponent } from './pages/client-details-page/client-details-page.component';
import { FormsModule, ReactiveFormsModule } from "@angular/forms";


@NgModule({
    declarations: [
        ClientsListPageComponent,
        ClientDetailsPageComponent
    ],
    imports: [
        CommonModule,
        ClientsRoutingModule,
        FormsModule,
        ReactiveFormsModule,
    ]
})
export class ClientsModule {
}
