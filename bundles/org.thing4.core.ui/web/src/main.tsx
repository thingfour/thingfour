import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import {Console} from "./Console.tsx";

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <Console />
    </StrictMode>,
)
