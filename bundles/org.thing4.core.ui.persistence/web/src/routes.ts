import {RouteEntry} from "@thing4/core-ui-api";
import {PersistenceManager} from "./PersistenceManager.tsx";
import {createElement} from "react";

const routes: RouteEntry[] = [
    {
        path: '/persistence-manager',
        element: createElement(PersistenceManager)
    }
];

export default routes;
