import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'
import {federation} from "@module-federation/vite";

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
            react(), federation({
                name: 'extension',
                filename: 'persistence-manager/module.js',
                exposes: {
                    './routes': './src/routes.ts',
                    './PersistencePage.tsx': './src/PersistenceManager.tsx'
                },
                //manifest: true,
                shared: ['react', 'react-dom']
            })
        ],
        base: buildEnv.BASE_URL,
        experimental: {
            renderBuiltUrl: filename => '../../' + filename
        },
        build: {
            target: 'esnext',
            minify: false,
            assetsDir: 'persistence-manager/assets',
            outDir: process.cwd() + '/dist',
        },
        server: {
            base: '/',
            port: 3001
        }
    }
})