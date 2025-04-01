import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'
import {federation} from "@module-federation/vite"
import dts from "vite-plugin-dts"

export default defineConfig(({ mode }) => {
    const env = loadEnv(mode, process.cwd(), '')
    const buildEnv = {
        BASE_URL: env.BASE_URL || '/',
        API_URL: env.API_URL || '/api/'
    };
    console.log(`Building application with environment: ${JSON.stringify(buildEnv)}`)

    return {
        // vite config
        assetsInclude: ['**/*.woff'],
        define : {
            __APP_ENV__: JSON.stringify(buildEnv),
            'process.env': JSON.stringify(buildEnv)
        },
        root: process.cwd() + '/src',
        plugins: [
            dts({
                insertTypesEntry: true,  // Ensures an entry `index.d.ts`
                outDir: "dist",          // Output directory for type definitions
                rollupTypes: true,       // Merges declarations into a single file
            }),
            react(), federation({
                name: "host",
                shared: [
                    "react", "react-dom", "@carbon/react"
                ],
                filename: "console.js"
            })
        ],
        base: buildEnv.BASE_URL,
        build: {
            target: 'esnext',
            outDir: process.cwd() + '/dist',
            lib: {
                entry: process.cwd() + '/src/index.ts',
                name: 'CoreUi',
                formats: ['es'],
                fileName: (format) => `index.${format}.js`,
            }
        },
        server: {
            base: '/',
            proxy: {
                '^/thing4/': {
                    target: 'http://localhost:8181/'
                    //changeOrigin: true,
                    //rewrite: (path) => path.replace(/^\/nbiot/, '')
                }
            }
        }
    }
})