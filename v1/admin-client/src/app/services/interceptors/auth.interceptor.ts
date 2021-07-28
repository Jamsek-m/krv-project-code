import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Observable } from "rxjs";
import { Injectable } from "@angular/core";
import { AuthService } from "@services";

@Injectable({
    providedIn: "root",
})
export class AuthInterceptor implements HttpInterceptor {

    constructor(private auth: AuthService) {
    }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const token = this.auth.getAccessToken();
        if (token !== null) {
            const headers = req.headers.set("Authorization", `Bearer ${token}`);
            return next.handle(req.clone({
                headers,
            }));
        }
        return next.handle(req);
    }

}
