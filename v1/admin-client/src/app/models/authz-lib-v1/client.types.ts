import { BaseType } from "./common.types";
import { SignatureAlgorithm } from "./key.types";

export enum ClientType {
    PUBLIC = "PUBLIC",
    BEARER_ONLY = "BEARER_ONLY",
    CONFIDENTIAL = "CONFIDENTIAL"
}

export enum ClientStatus {
    ENABLED = "ENABLED",
    DISABLED = "DISABLED"
}

export enum PKCEMethod {
    NONE = "",
    S256 = "S256",
    PLAIN = "plain",
}

export interface Client extends BaseType {
    name: string;
    clientId: string;
    type: ClientType;
    status: ClientStatus;
    pkceMethod: string;
    redirectUris: string[];
    webOrigins: string[];
    secret: string;
    requireConsent: boolean;
    scopes: string[];
    signingKeyAlgorithm: SignatureAlgorithm;
}
