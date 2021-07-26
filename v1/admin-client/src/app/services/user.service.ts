import { Inject, Injectable } from "@angular/core";
import { ADMIN_API_URL } from "../injectables";
import { HttpClient, HttpResponse } from "@angular/common/http";
import { Observable } from "rxjs";
import { EntityList } from "@mjamsek/prog-utils";
import { User } from "../models";
import { map } from "rxjs/operators";
import { mapToEntityList } from "../utils/list.utils";

@Injectable({
    providedIn: "root"
})
export class UserService {

    constructor(@Inject(ADMIN_API_URL) private apiUrl: string,
                private http: HttpClient) {
    }

    public getUsers(): Observable<EntityList<User>> {
        const url = `${this.apiUrl}/users`;
        const params = {
            limit: 10,
            offset: 0,
        };
        return this.http.get(url, {observe: "response", params}).pipe(
            map(res => res as HttpResponse<User[]>),
            map(mapToEntityList)
        );
    }

    public getUser(userId: string): Observable<User> {
        const url = `${this.apiUrl}/users/${userId}`;
        return this.http.get(url).pipe(
            map(res => res as User),
        );
    }

}
