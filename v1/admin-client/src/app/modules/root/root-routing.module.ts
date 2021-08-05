import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { LayoutComponent } from "./layout/layout.component";
import { LandingPageComponent } from "./pages/landing-page/landing-page.component";
import { Error404PageComponent } from "./pages/error404-page/error404-page.component";
import { Error403PageComponent } from "./pages/error403-page/error403-page.component";
import { OidcCallbackPageComponent } from "./pages/oidc-callback-page/oidc-callback-page.component";
import { AdminChildGuard } from "@services";

const routes: Routes = [
    {
        path: "", component: LayoutComponent, children: [
            {path: "", component: LandingPageComponent, pathMatch: "full"},
            {path: "clients", loadChildren: () => import("../clients/clients.module").then(m => m.ClientsModule), canActivateChild: [AdminChildGuard]},
            {path: "keys", loadChildren: () => import("../keys/keys.module").then(m => m.KeysModule), canActivateChild: [AdminChildGuard]},
            {path: "roles", loadChildren: () => import("../roles/roles.module").then(m => m.RolesModule), canActivateChild: [AdminChildGuard]},
            {path: "settings", loadChildren: () => import("../settings/settings.module").then(m => m.SettingsModule), canActivateChild: [AdminChildGuard]},
            {path: "users", loadChildren: () => import("../users/users.module").then(m => m.UsersModule), canActivateChild: [AdminChildGuard]},
            {path: "404", component: Error404PageComponent},
            {path: "403", component: Error403PageComponent},
        ]
    },
    {path: "callback/oidc", pathMatch: "full", component: OidcCallbackPageComponent},
    {path: "**", redirectTo: "/404"}
];

@NgModule({
    imports: [RouterModule.forRoot(routes, {
        paramsInheritanceStrategy: "always",
    })],
    exports: [RouterModule]
})
export class RootRoutingModule {
}
