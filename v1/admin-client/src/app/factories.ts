import { ProviderContext } from "@context";

export function AppConfigFactory(provider: ProviderContext) {
    return () => {
        provider.loadWellKnownConfig();
    };
}
