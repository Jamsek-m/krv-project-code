import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { LayoutComponent } from "./layout/layout.component";
import { LandingPageComponent } from "./pages/landing-page/landing-page.component";
import { Error404PageComponent } from "./pages/error404-page/error404-page.component";
import { Error403PageComponent } from "./pages/error403-page/error403-page.component";

const routes: Routes = [
    {
        path: "", component: LayoutComponent, children: [
            {path: "", component: LandingPageComponent, pathMatch: "full"},
            {path: "clients", loadChildren: () => import("../clients/clients.module").then(m => m.ClientsModule)},
            {path: "404", component: Error404PageComponent},
            {path: "403", component: Error403PageComponent},
        ]
    },
    {path: "**", redirectTo: "/404"}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class RootRoutingModule {
}
