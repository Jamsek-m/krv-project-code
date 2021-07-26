import { Component, OnInit } from "@angular/core";
import { menuItems } from "../../../../config/menu.config";
import { NavbarContext } from "../../../../context";

@Component({
    selector: "ew-header",
    templateUrl: "./header.component.html",
    styleUrls: ["./header.component.scss"]
})
export class HeaderComponent implements OnInit {

    public menuItems = menuItems;

    constructor(public nav: NavbarContext) {

    }

    ngOnInit(): void {
    }

}
