"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
const express_2 = require("express");
const cors_1 = __importDefault(require("cors"));
const path_1 = __importDefault(require("path"));
const dotenv_1 = __importDefault(require("dotenv"));
const admin = __importStar(require("firebase-admin"));
dotenv_1.default.config();
const port = 3000;
const routes = (0, express_2.Router)();
const app = (0, express_1.default)();
app.use((0, cors_1.default)());
app.use(express_1.default.static("public"));
app.use(routes);
const firebaseConfig = {
    apiKey: "AIzaSyAsdNeVhKB-kYlwmnry-XPJfojHbs-5zIc",
    authDomain: "superid-e53fb.firebaseapp.com",
    projectId: "superid-e53fb",
    appId: "1:459214844582:web:63d89c9f5692be2533ba67",
};
admin.initializeApp();
const db = admin.firestore();
routes.post("/api/perform-auth", async (req, res) => {
    const site = "www.donabet.com.br";
    const apiKey = process.env.PARTNER_API_KEY;
    if (!apiKey) {
        return res.status(500).json({ error: "API Key não configurada no backend." });
    }
    try {
        const result = await performAuth({ site, apiKey });
        res.json(result.data);
    }
    catch (error) {
        console.error("Erro ao chamar performAuth:", error.message);
        res.status(500).json({ error: "Erro interno ao gerar QR Code." });
    }
});
routes.get('/', (req, res) => {
    res.statusCode = 403;
    res.send('Acesso não permitido. Rota default não definida.');
});
routes.get("/loginSuperId", (req, res) => {
    res.sendFile(path_1.default.join(__dirname, "../public/pages/signInWithSuperID.html"));
});
app.listen(port, () => {
    console.log(`Server is running on: ${port}`);
});
