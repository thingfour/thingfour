import { Plugin } from "@thing4/core-ui-api";
import { createContext, useContext, useEffect, useState } from "react";

type RemoteContextType = {
    extensions: Record<string, Plugin[]>;
};

const ExtensionPointContext = createContext<RemoteContextType>({ extensions: {} });

export const RemoteModuleProvider = ({ children }: { children: React.ReactNode }) => {
    const [extensions, setExtensions] = useState<Record<string, Plugin[]>>({});

    useEffect(() => {
        (async () => {
            try {
                const response = await fetch("/thing4/ui/plugins");
                const remoteExtensions: { plugins: Plugin[]; } = await response.json();

                const loadedExtensions: Record<string, Plugin[]> = {};

                // extensionPointId: string;
                // module: string;
                // pluginId: string;
                for (const plugin of remoteExtensions.plugins) {
                    try {
                        const mod = await import(`/thing4/modules/${plugin.module}/module.js`);
                        if (!mod.default || !Array.isArray(mod.default)) {
                            console.warn(`Invalid module format: ${module}`);
                            continue;
                        }
                        loadedExtensions[plugin.extensionPointId] = (loadedExtensions[plugin.extensionPointId] || []).concat(mod.default);
                    } catch (error) {
                        console.error(`Failed to load module ${module}:`, error);
                    }
                }

                setExtensions(loadedExtensions);
            } catch (error) {
                console.error("Error loading remote modules:", error);
            }
        })();
    }, []);

    return <ExtensionPointContext.Provider value={{ extensions }}>{children}</ExtensionPointContext.Provider>;
};

export const useExtensions = <K extends string>(kind: K): Plugin[] => {
    const { extensions } = useContext(ExtensionPointContext);
    return extensions[kind] || [];
};