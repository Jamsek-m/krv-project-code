import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";

import { ClientsRoutingModule } from "./clients-routing.module";
import { ClientsListPageComponent } from "./pages/clients-list-page/clients-list-page.component";
import { ClientDetailsPageComponent } from './pages/client-details-page/client-details-page.component';
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { CreateClientPageComponent } from './pages/create-client-page/create-client-page.component';
import { SharedModule } from "../shared/shared.module";
import { ClientGeneralTabComponent } from './pages/client-details-page/tabs/client-general-tab/client-general-tab.component';
import { ClientSigningTabComponent } from './pages/client-details-page/tabs/client-signing-tab/client-signing-tab.component';
import { ClientScopesTabComponent } from './pages/client-details-page/tabs/client-scopes-tab/client-scopes-tab.component';


@NgModule({
    declarations: [
        ClientsListPageComponent,
        ClientDetailsPageComponent,
        CreateClientPageComponent,
        ClientGeneralTabComponent,
        ClientSigningTabComponent,
        ClientScopesTabComponent
    ],
    imports: [
        CommonModule,
        ClientsRoutingModule,
        FormsModule,
        ReactiveFormsModule,
        SharedModule,
    ]
})
export class ClientsModule {
}
