import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Observable } from "rxjs";
import { Injectable } from "@angular/core";
import { catchError, switchMap } from "rxjs/operators";
import { AuthService } from "@services";

@Injectable({
    providedIn: "root",
})
export class AuthInterceptor implements HttpInterceptor {

    constructor(private auth: AuthService) {
    }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return this.auth.getAccessToken().pipe(
            switchMap((accessToken: string | null) => {
                if (accessToken !== null) {
                    const headers = req.headers.set("Authorization", `Bearer ${accessToken}`);
                    return next.handle(req.clone({
                        headers,
                    }));
                }
                return next.handle(req);
            }),
            catchError((err) => {
                // console.error(err);
                return next.handle(req);
            })
        );
    }

}
