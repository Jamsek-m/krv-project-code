import { NgModule } from "@angular/core";
import { KeysListPageComponent } from "./pages/keys-list-page/keys-list-page.component";
import { CommonModule } from "@angular/common";
import { SharedModule } from "../shared/shared.module";
import { KeysRoutingModule } from "./keys-routing.module";
import { ReactiveFormsModule } from "@angular/forms";
import { VerificationKeyPopupComponent } from './components/verification-key-popup/verification-key-popup.component';

@NgModule({
    imports: [
        CommonModule,
        SharedModule,
        KeysRoutingModule,
        ReactiveFormsModule,
    ],
    declarations: [
        KeysListPageComponent,
        VerificationKeyPopupComponent
    ]
})
export class KeysModule {

}
