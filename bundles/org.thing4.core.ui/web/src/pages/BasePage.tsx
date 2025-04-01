import {Theme} from '@carbon/react';
import {ReactNode} from "react";
import { Link } from 'react-router-dom';

export interface BasePageProps {
  title: string;
  children: ReactNode;
}

export function BasePage( { title, children } : BasePageProps) {

    const navigationItems = [
        { path: "/", label: "Home" },
        { path: "/persistence-manager", label: "Persistence Manager" },
    ];

    return <Theme theme={"g100"}>
        <h1>{title}</h1>

        <ul>
            {navigationItems.map(({ path, label }) => (
                <li key={path}>
                    <Link to={path}>{label}</Link>
                </li>
            ))}
        </ul>

        {children}
    </Theme>

}