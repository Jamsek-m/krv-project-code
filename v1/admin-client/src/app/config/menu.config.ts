import { MenuItem } from "@lib";

export const menuItems: MenuItem[] = [
    {
        label: "Home",
        url: "/",
        external: false,
    },
    {
        label: "Clients",
        url: "/clients",
        external: false,
        requireAuth: true,
        requiredScopes: ["admin"]
    },
    {
        label: "Users",
        url: "/users",
        external: false,
        requireAuth: true,
        requiredScopes: ["admin"]
    },
    {
        label: "Roles",
        url: "/roles",
        external: false,
        requireAuth: true,
        requiredScopes: ["admin"]
    },
    {
        label: "Keys",
        url: "/keys",
        external: false,
        requireAuth: true,
        requiredScopes: ["admin"]
    },
    {
        label: "Settings",
        url: "/settings",
        external: false,
        requireAuth: true,
        requiredScopes: ["admin"]
    },
];
