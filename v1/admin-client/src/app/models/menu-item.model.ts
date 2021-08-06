export interface MenuItem {
    label: string;
    desc?: string;
    url: string;
    external?: boolean;
    requireAuth?: boolean;
    requiredScopes?: string[];
}
