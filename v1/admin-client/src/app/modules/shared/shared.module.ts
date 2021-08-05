import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { ToastrModule } from "ngx-toastr";
import { TabsModule } from "ngx-bootstrap/tabs";
import { ModalModule } from "ngx-bootstrap/modal";
import { SafeUrlPipe } from "./pipes/safe-url/safe-url.pipe";
import { TooltipModule } from "ngx-bootstrap/tooltip";
import { ShortUuidPipe } from "./pipes/short-uuid/short-uuid.pipe";


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
        ModalModule.forRoot(),
        TabsModule.forRoot(),
        TooltipModule.forRoot(),
    ],
    declarations: [
        SafeUrlPipe,
        ShortUuidPipe
    ],
    exports: [
        TabsModule,
        ModalModule,
        SafeUrlPipe,
        TooltipModule,
        ShortUuidPipe,
    ]
})
export class SharedModule {

}
