export function getCookieByName(name: string): string | null {
    name = name + "=";
    const cookies = document.cookie.split(";");
    for (let cookie of cookies) {
        cookie = cookie.trim();
        if (cookie.startsWith(name)) {
            return cookie.substr(name.length, cookie.length);
        }
    }
    return null;
}
