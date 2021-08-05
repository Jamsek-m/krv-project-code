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
    },
    {
        label: "Users",
        url: "/users",
        external: false,
        requireAuth: true,
    },
    {
        label: "Roles",
        url: "/roles",
        external: false,
        requireAuth: true,
    },
    {
        label: "Keys",
        url: "/keys",
        external: false,
        requireAuth: true,
    },
    {
        label: "Settings",
        url: "/settings",
        external: false,
        requireAuth: true,
    },
];
