import { BaseType } from "./common.types";
import { SignatureAlgorithm } from "./key.types";

export enum KeyType {
    HMAC = "HMAC",
    RSA = "RSA",
    ELLIPTIC_CURVE = "EC"
}

export interface WellKnownConfig {
    issuer: string;
    authorization_endpoint: string;
    token_endpoint: string;
    userinfo_endpoint: string;
    end_session_endpoint: string;
    jwks_uri: string;
    grant_types_supported: string[];
    id_token_signing_alg_values_supported: string[];
    userinfo_signing_alg_values_supported: string[];
    token_endpoint_auth_signing_alg_values_supported: string[];
    claims_supported: string[];
    scopes_supported: string[];
    code_challenge_methods_supported: string[];
}

export interface JsonWebKey {
    kid: string;
    kty: string;
    alg: string;
    use: string;
    e?: string;
    n?: string;
    x?: string;
    y?: string;
    crv?: string;
}

export interface JWKS {
    keys: JsonWebKey[];
}

export interface PublicSigningKey extends BaseType {
    keyType: KeyType;
    priority: number;
    algorithm: SignatureAlgorithm;
}

export interface PKCEChallenge {
    code_verifier: string;
    code_challenge: string;
    code_challenge_method: PKCEChallenge.PKCEMethod;
}

export namespace PKCEChallenge {
    export type PKCEMethod = "S256" | "plain";
    export const PKCEMethod = {
        S256: "S256" as PKCEMethod,
        plain: "plain" as PKCEMethod,
    };
}
