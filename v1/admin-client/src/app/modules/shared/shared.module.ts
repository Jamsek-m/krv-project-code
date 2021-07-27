import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { ToastrModule } from "ngx-toastr";
import { TabsModule } from "ngx-bootstrap/tabs";


@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        ToastrModule.forRoot({
            tapToDismiss: true,
            closeButton: true,
            newestOnTop: true,
            maxOpened: 3,
        }),
        TabsModule.forRoot(),
    ],
    declarations: [

    ],
    exports: [
        TabsModule,
    ]
})
export class SharedModule {

}
