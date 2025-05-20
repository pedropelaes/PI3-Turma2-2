import express from "express"
import {Request, Response, Router} from "express";
import cors from "cors"
import path from "path";
import dotenv from "dotenv"
import * as admin from "firebase-admin";
import fetch from "node-fetch"

dotenv.config()
const port = 3000;
const routes = Router();
const app = express();
app.use(cors());
app.use(express.json());
app.use(express.static("public"));
app.use(routes);

const firebaseConfig = {
  apiKey: "AIzaSyAsdNeVhKB-kYlwmnry-XPJfojHbs-5zIc",
  authDomain: "superid-e53fb.firebaseapp.com",
  projectId: "superid-e53fb",
  appId: "1:459214844582:web:63d89c9f5692be2533ba67",
};

admin.initializeApp();

routes.post("/api/perform-auth", async (req: Request, res: Response) => {
  const { site } = req.body;
  const apiKey = process.env.PARTNER_API_KEY;

  try {
    const response = await fetch("https://performauth-snp2owcvrq-rj.a.run.app", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ site, apiKey })
    });

    const data = await response.json();
    res.json(data);
  } catch (error: any) {
    console.error("Erro ao chamar performAuth:", error.message);
    res.status(500).json({ error: "Erro ao gerar QR Code." });
  }
});

routes.get('/', (req: Request, res: Response)=>{
    res.statusCode = 403;
    res.send('Acesso não permitido. Rota default não definida.');
});

routes.get("/loginSuperId", (req: Request, res: Response) => {
  res.sendFile(path.join(__dirname, "../public/pages/signInWithSuperID.html"));
});


app.listen(port, ()=>{
    console.log(`Server is running on: ${port}`)
})