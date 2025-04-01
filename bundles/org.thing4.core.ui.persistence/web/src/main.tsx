import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'

import {PersistenceManager} from "./PersistenceManager.tsx";

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <PersistenceManager />
    </StrictMode>,
)
