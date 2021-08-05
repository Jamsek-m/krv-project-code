import { PKCEChallenge, WellKnownConfig } from "./authz-lib-v1";

export enum AuthStateStatus {
    AUTHENTICATED,
    NO_TOKENS,
    NO_SESSION,
}

export type LoginRequest = {
    wellKnown: WellKnownConfig;
    pkceChallenge: PKCEChallenge;
}

export interface TokenInfo {
    subject: string;
    username: string;
    name: string;
    expiresAt: Date;
    email: string;
    sessionState: string;
}

type AuthenticatedState = {
    status: AuthStateStatus.AUTHENTICATED;
    accessToken: string;
    refreshToken: string;
    idToken: string;
    parsedAccessToken: TokenInfo;
    sessionState: string;
    scopes: string[];
}

export type AuthState =
    | { status: AuthStateStatus.NO_SESSION }
    | { status: AuthStateStatus.NO_TOKENS }
    | AuthenticatedState;
