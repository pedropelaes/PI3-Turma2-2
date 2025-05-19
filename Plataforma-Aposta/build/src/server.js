"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
const express_2 = require("express");
const cors_1 = __importDefault(require("cors"));
const path_1 = __importDefault(require("path"));
const port = 3000;
const routes = (0, express_2.Router)();
var app = (0, express_1.default)();
app.use((0, cors_1.default)());
app.use(express_1.default.static("public"));
app.use(routes);
routes.get('/', (req, res) => {
    res.statusCode = 403;
    res.send('Acesso não permitido. Rota default não definida.');
});
routes.post("/loginSuperId", (req, res) => {
    res.sendFile(path_1.default.join(__dirname, "../public/pages/signInWithSuperID.html"));
});
app.listen(port, () => {
    console.log(`Server is running on: ${port}`);
});
