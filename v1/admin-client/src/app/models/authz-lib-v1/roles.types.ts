import { BaseType } from "./common.types";

export interface Role extends BaseType {
    name: string;
    description: string;
    grantedScopes: string[];
}

export interface RoleGrantRequest {
    userId: string;
    roleId: string;
}
