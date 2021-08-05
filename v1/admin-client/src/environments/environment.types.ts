export interface VersionedApiUrl {
    baseUrl: string;
    contextPath: string;
}

export interface AuthConfig {
    wellKnownEndpoint: string;
    clientId: string;
    scopes: string[];
    redirectUri: string;
    postLogoutRedirectUri: string;
    settings?: {
        refreshTokenSecondsBefore?: number;
        checkSessionEverySeconds?: number;
    }
}

export interface AppEnvironment {
    production: boolean;
    apis?: {
        admin: VersionedApiUrl;
        [apiName: string]: VersionedApiUrl;
    };
    auth: AuthConfig;
}
