import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";

import { RootRoutingModule } from "./root-routing.module";
import { LandingPageComponent } from "./pages/landing-page/landing-page.component";
import { HeaderLinkComponent } from "./components/header/header-link/header-link.component";
import { HeaderComponent } from "./components/header/header.component";
import { LayoutComponent } from "./layout/layout.component";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { SharedModule } from "../shared/shared.module";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { HttpClientModule } from "@angular/common/http";
import { RouterModule } from "@angular/router";
import { OidcCallbackPageComponent } from './pages/oidc-callback-page/oidc-callback-page.component';
import { SessionCheckComponent } from './components/session-check/session-check.component';
import { ErrorPageComponent } from './pages/error-page/error-page.component';


@NgModule({
    declarations: [
        LandingPageComponent,
        LayoutComponent,
        HeaderComponent,
        HeaderLinkComponent,
        OidcCallbackPageComponent,
        SessionCheckComponent,
        ErrorPageComponent,
    ],
    imports: [
        CommonModule,
        BrowserAnimationsModule,
        HttpClientModule,
        FormsModule,
        ReactiveFormsModule,
        SharedModule,
        RouterModule,
        RootRoutingModule,
    ],
    exports: [
        RouterModule,
    ]
})
export class RootModule {
}
