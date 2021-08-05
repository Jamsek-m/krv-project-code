import { Inject, Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { combineLatest, Observable } from "rxjs";
import { map } from "rxjs/operators";
import { ADMIN_API_URL } from "@injectables";
import { Role, RoleGrantRequest, AggregatedRoles } from "@lib";
import { arrayDifference } from "@utils";
import { UserService } from "./user.service";

@Injectable({
    providedIn: "root"
})
export class RolesService {

    constructor(@Inject(ADMIN_API_URL) private apiUrl: string,
                private userService: UserService,
                private http: HttpClient) {
    }

    public getAggregatedUserRoles(userId: string): Observable<AggregatedRoles> {
        return combineLatest([
            this.userService.getUserRoles(userId),
            this.getRoles(),
        ]).pipe(
            map((result) => {
                const [user, all] = result;
                return {
                    user,
                    all: arrayDifference(all, user, (r1, r2) => r1.id === r2.id),
                    scopes: RolesService.getAppliedScopes(user),
                }
            })
        );
    }

    public getRoles(): Observable<Role[]> {
        const url = `${this.apiUrl}/roles`;
        return this.http.get(url).pipe(
            map(res => res as Role[]),
        );
    }

    public getRole(roleId: string): Observable<Role> {
        const url = `${this.apiUrl}/roles/${roleId}`;
        return this.http.get(url).pipe(
            map(res => res as Role),
        );
    }

    public createRole(role: Partial<Role>): Observable<Role> {
        const url = `${this.apiUrl}/roles`;
        return this.http.post(url, role).pipe(
            map(res => res as Role),
        );
    }

    public patchRole(roleId: string, request: Partial<Role>): Observable<Role> {
        const url = `${this.apiUrl}/roles/${roleId}`;
        return this.http.patch(url, request).pipe(
            map(res => res as Role),
        );
    }

    public deleteRole(roleId: string): Observable<void> {
        const url = `${this.apiUrl}/roles/${roleId}`;
        return this.http.delete(url).pipe(
            map(res => res as unknown as void),
        );
    }

    public assignRoleToUser(userId: string, roleId: string): Observable<void> {
        const url = `${this.apiUrl}/roles/assign`;
        const request: RoleGrantRequest = {
            userId,
            roleId,
        };
        return this.http.post(url, request).pipe(
            map(res => res as unknown as void),
        );
    }

    public unassignRoleToUser(userId: string, roleId: string): Observable<void> {
        const url = `${this.apiUrl}/roles/unassign`;
        const request: RoleGrantRequest = {
            userId,
            roleId,
        };
        return this.http.request("DELETE", url, {
            body: request,
        }).pipe(
            map(res => res as unknown as void),
        );
    }

    private static getAppliedScopes(userRoles: Role[]): string[] {
        const scopeSet = userRoles.map(role => {
            return role.grantedScopes;
        }).reduce((acc: Set<string>, current: string[]) => {
            current.forEach(scope => {
                acc.add(scope);
            })
            return acc;
        }, new Set<string>());
        return [...scopeSet];
    }
}
