import { Component, Input, OnInit } from "@angular/core";
import { MenuItem } from "../../../../../models";


@Component({
    selector: "ew-header-link",
    templateUrl: "./header-link.component.html",
    styleUrls: ["./header-link.component.scss"]
})
export class HeaderLinkComponent implements OnInit {

    @Input()
    public item: MenuItem;

    constructor() {
    }

    ngOnInit(): void {
    }

}
