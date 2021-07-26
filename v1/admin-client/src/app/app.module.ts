import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";

import { AppComponent } from "./app.component";
import { RootModule } from "./modules/root/root.module";

@NgModule({
    declarations: [
        AppComponent
    ],
    imports: [
        BrowserModule,
        RootModule,
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule {
}
