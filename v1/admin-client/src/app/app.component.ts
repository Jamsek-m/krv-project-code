import { Component, OnInit } from "@angular/core";
import { Meta } from "@angular/platform-browser";
import { environment } from "@environment/environment";

@Component({
    selector: "az-root",
    templateUrl: "./app.component.html"
})
export class AppComponent implements OnInit {

    constructor(private meta: Meta) {
    }

    ngOnInit() {
        this.meta.addTag({
            name: "env",
            content: environment.env,
        }, true);
    }

}
