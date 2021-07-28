import { Component, OnInit } from "@angular/core";
import { AuthService } from "../../../../services/auth.service";

@Component({
    selector: "ew-landing-page",
    templateUrl: "./landing-page.component.html",
    styleUrls: ["./landing-page.component.scss"]
})
export class LandingPageComponent implements OnInit {

    constructor(public auth: AuthService) {
    }

    ngOnInit(): void {
    }

}
