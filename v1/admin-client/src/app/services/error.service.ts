import { Injectable } from "@angular/core";
import { Observable, of, throwError } from "rxjs";
import { PageError } from "@lib";
import { pageErrors } from "../config/error.config";

@Injectable({
    providedIn: "root"
})
export class ErrorService {

    public getError(status: string): Observable<PageError> {
        const error = pageErrors.find(err => err.status === status);
        if (error) {
            return of(error);
        }
        return throwError(new Error("Missing configuration for provided error!"));
    }

}
