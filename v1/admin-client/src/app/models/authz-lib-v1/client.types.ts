import { BaseType } from "./common.types";

export enum ClientType {
    PUBLIC = "PUBLIC",
    BEARER_ONLY = "BEARER_ONLY",
    CONFIDENTIAL = "CONFIDENTIAL"
}

export enum ClientStatus {
    ENABLED = "ENABLED",
    DISABLED = "DISABLED"
}

export class Client extends BaseType {
    public name: string;
    public clientId: string;
    public type: ClientType;
    public status: ClientStatus;
    public redirectUris: string[];
    public secret: string;
    public requireConsent: boolean;
    public scopes: string[];
}
