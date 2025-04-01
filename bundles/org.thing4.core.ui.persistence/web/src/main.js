"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const react_1 = __importDefault(require("react"));
const client_1 = __importDefault(require("react-dom/client"));
const Login_1 = __importDefault(require("./Login"));
const react_router_dom_1 = require("react-router-dom");
const basePath = document.querySelector('base').getAttribute('href');
const apiPath = process.env.API_URL;
console.log(`Starting application at ${basePath}, and API ${apiPath}`);
client_1.default.createRoot(document.getElementById('root')).render(<react_1.default.StrictMode>
        <react_router_dom_1.BrowserRouter basename={basePath}>
            <Login_1.default />
        </react_router_dom_1.BrowserRouter>
    </react_1.default.StrictMode>);
