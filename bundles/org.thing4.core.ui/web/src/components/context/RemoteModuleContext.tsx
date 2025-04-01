import {
    createContext,
    useContext,
    useState,
    ReactNode,
    Suspense,
    lazy,
    ComponentType, useMemo, LazyExoticComponent
} from "react";
import { ErrorBoundary } from "../ErrorBoundary";

interface RemoteModuleContextType {
    remoteRoutes: RouterConfig[];
    loadRemoteModules: (configUrl: string) => Promise<void>;
}

const RemoteModuleContext = createContext<RemoteModuleContextType | undefined>(undefined);

interface LoaderProps {
    module: string,
    elementName: string
}

function loadRemote<T>(path: string, file: string): Promise<T> {
    return import(/* @vite-ignore */ path)
        .then(r => {
            console.log(`Imported module from ${path}:`, r); // Debugging log

            if (!r.get) {
                console.error(`No "get" function found in ${path}`);
                throw new Error(`Module ${path} does not expose "get"`);
            }

            return r.get(file) as Promise<T>;
        })
        .catch(error => {
            console.error(`Failed to load ${path} and ${file}`, error);
            return Promise.resolve({
                default: () => (
                    <div>
                        Failed to load page {module} ({path}) – {error?.message ?? 'no message'}
                    </div>
                ),
            } as T)
        })
}

function resolve(module: string, path: string) {
    return loadRemote<{ default: ComponentType }>(module, path)
        .then(
            (v: { default: ComponentType }) => v,
        ).catch((error) => ({
            default: (props: unknown) => (
                <div>
                    Failed to load page {module} ({path} {JSON.stringify(props)}) (
                    {error?.message ?? ''})
                </div>
            ),
        }));
}

const cache: Record<string, LazyExoticComponent<ComponentType>> = {};

function importRemote({module, elementName}: LoaderProps) {
    const Component = useMemo(() =>
            cache[`${module}-${elementName}`] ??
            (cache[`${module}-${elementName}`] = lazy<ComponentType>(() => resolve(module, elementName))),
        [module, elementName]
    );

    return (
        <ErrorBoundary>
            <Suspense
                fallback={
                    <div>
                        Failed to load page {module} {elementName}
                    </div>
                }
            >
                <Component />
            </Suspense>
        </ErrorBoundary>
    );
}

export function RawPageLoad({module, elementName}: LoaderProps): JSX.Element {
    return importRemote({module, elementName});
}

interface RouterConfig {
    module: string;
    path: string;
    elementName: string;
}

export const RemoteModuleProvider = ({children}: { children: ReactNode }) => {
    const [remoteRoutes, setRemoteRoutes] = useState<RouterConfig[]>([]);

    const loadRemoteModules = async (configUrl: string) => {
        console.log(`Requesting data from ${configUrl}`)
        try {
            const response = await fetch(configUrl);
            const remotes: { modules: { module: string, path: string }[] } = await response.json();

            console.log(`Loaded data for ${configUrl}`, remotes);

            for (const remote of remotes.modules) {
                try {
                    console.log(`Loading module: ${remote.module} from ${remote.path}`);
                    setRemoteRoutes(prev => [...prev, {
                        module: remote.module,
                        path: '/thing4/ui/modules/persistence-manager',
                        elementName: './PersistencePage.tsx'
                    }])
                    // console.log('Loaded routes', remoteRoutes)
                    // if (remoteRoutes) {
                    //     setRemoteRoutes((prev) => [...prev, ...remoteRoutes]);
                    // } else {
                    //     console.error(`Default export not found in ${remote.path}`);
                    // }
                } catch (error) {
                    console.error(`Error loading module ${remote.module} from ${remote.path}:`, error);
                }
            }
        } catch (error) {
            console.error("Failed to fetch remote modules configuration:", error);
        }
    };

    return (
        <RemoteModuleContext.Provider value={{remoteRoutes, loadRemoteModules}}>
            {children}
        </RemoteModuleContext.Provider>
    );
};


export const useRemoteModules = () => {
    const context = useContext(RemoteModuleContext);
    if (!context) {
        throw new Error("useRemoteRoutes must be used within a RemoteModuleProvider");
    }
    return context;
};
