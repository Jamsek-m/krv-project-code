import { PageError } from "@lib";

export const pageErrors: PageError[] = [
    {
        status: "403",
        title: "Missing permission to access admin console!",
        description: "Current user lacks required permission to access this page. Contact administrator to obtain required permissions."
    },
    {
        status: "404",
        title: "Page does not exist!",
        description: "Requested page does not exist."
    }
];
