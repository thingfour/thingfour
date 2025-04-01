import {BrowserRouter, Route, Routes} from "react-router-dom";

import {RawPageLoad, RemoteModuleProvider, useRemoteModules} from "./components/context/RemoteModuleContext.tsx";
import {BasePage} from "./pages/BasePage.tsx";
import {useEffect} from "react";

// @ts-ignore
import './Console.scss';


const basePath = document.querySelector('base')?.getAttribute('href') || '/';
const apiPath = process.env.API_URL || '/';
console.log(`Starting application at ${basePath}, and API ${apiPath}`);


const ConsoleRoutes = () => {
    const { remoteRoutes, loadRemoteModules } = useRemoteModules();

    let configUrl = "/thing4/ui/modules.json";
    useEffect(() => {
        // Load remote modules from a server-provided JSON configuration
        loadRemoteModules(configUrl);
    }, [configUrl]);

    console.log(`Loaded extensions`, remoteRoutes);

    return (
        <Routes>
            <Route path="/" element={<BasePage title="Home">Welcome!</BasePage>} />
            {remoteRoutes.map(({ module, elementName }) => (
                <Route key={module} path={module} element={
                    <RawPageLoad module={'/thing4/ui/modules/' + module + '/module.js'} elementName={elementName} />
                } />
            ))}
        </Routes>
    );
};


export function Console() {
    return <>
        <RemoteModuleProvider>
            <BrowserRouter basename={basePath}>
                <ConsoleRoutes />
            </BrowserRouter>
        </RemoteModuleProvider>
    </>
}