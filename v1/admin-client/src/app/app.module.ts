import { APP_INITIALIZER, NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";

import { AppComponent } from "./app.component";
import { RootModule } from "./modules/root/root.module";
import { AppConfigFactory } from "./factories";
import { HTTP_INTERCEPTORS } from "@angular/common/http";
import { AuthInterceptor } from "./services/interceptors/auth.interceptor";
import { ProviderContext } from "@context";

@NgModule({
    declarations: [
        AppComponent
    ],
    imports: [
        BrowserModule,
        RootModule,
    ],
    providers: [
        {provide: APP_INITIALIZER, useFactory: AppConfigFactory, multi: true, deps: [ProviderContext]},
        {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
