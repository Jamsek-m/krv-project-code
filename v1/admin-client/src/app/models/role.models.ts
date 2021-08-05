import { Role } from "./authz-lib-v1";

export interface AggregatedRoles {
    all: Role[];
    user: Role[];
    scopes: string[];
}
