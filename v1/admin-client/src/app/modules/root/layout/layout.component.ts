import { Component, OnInit } from "@angular/core";
import { NavigationEnd, Router } from "@angular/router";
import { Title } from "@angular/platform-browser";
import { menuItems } from "../../../config/menu.config";
import { MenuItem } from "@lib";
import { AuthService } from "@services";

@Component({
    selector: "mj-layout",
    templateUrl: "./layout.component.html",
    styleUrls: ["./layout.component.scss"]
})
export class LayoutComponent implements OnInit {

    constructor(private router: Router,
                private title: Title,
                private auth: AuthService) {
    }

    ngOnInit() {
        this.setPageTitle();
    }

    private setPageTitle() {
        this.router.events.subscribe((routerEvent) => {
            if (routerEvent instanceof NavigationEnd) {
                if (routerEvent.url === "/") {
                    this.title.setTitle("");
                } else {
                    const item = menuItems.find((menuItem: MenuItem) => {
                        return menuItem.url === routerEvent.url;
                    });
                    if (item) {
                        this.title.setTitle(`${item.label}`);
                    } else {
                        this.title.setTitle("");
                    }
                }
            }
        });
    }

}
