import { onCall, onRequest } from "firebase-functions/v2/https";
import * as admin from "firebase-admin";
import { Request, Response } from "express";
const QRCode = require("qrcode");

admin.initializeApp();

export const checkEmailVerificationV2 = onCall({region: "southamerica-east1"},
  async (request) => {
    const email = (request.data?.email || "").trim().toLowerCase();

    if (!email) {
      throw new Error("E-mail não fornecido.");
    }

    const userRecord = await admin.auth().getUserByEmail(email);
    return { verified: userRecord.emailVerified };
  }
);

const db = admin.firestore();

export const performAuth = onRequest({region: "southamerica-east1"},(req: Request, res: Response): void => {
  (async () => {
    const { site, apiKey } = req.body;

    if (!site || !apiKey) {
      res.status(400).json({ error: "site e apiKey são obrigatórios." });
      return;
    }

    const snapshot = await db.collection("partners")
      .where("url", "==", site)
      .limit(1)
      .get();

    if (snapshot.empty) throw new Error("Parceiro não encontrado.");

    const partnerData = snapshot.docs[0].data();

    if (partnerData?.apiKey !== apiKey) {
      throw new Error("API Key inválida.");
    }

    const loginToken = [...Array(256)].map(() => Math.random().toString(36)[2]).join('');

    await db.collection("login").doc(loginToken).set({
      site,
      apiKey,
      loginToken,
      createdAt: admin.firestore.FieldValue.serverTimestamp()
    });

    const qrCodeImage = await QRCode.toDataURL(loginToken);
    res.status(200).json({ qrCodeImage: qrCodeImage, loginToken: loginToken });
  })().catch(error => {
    console.error("Erro interno:", error);
    res.status(500).json({ error: "Erro interno no servidor." });
  });
});

export const getLoginStatus = onRequest({ region: "southamerica-east1" }, (req: Request, res: Response): void => {
  (async () => {
    const { loginToken } = req.body;
    
    if (!loginToken) {
      return res.status(400).json({ error: "uid é parametro obrigatório" });
    }

    const snapshot = await db.collection("login")
      .where("loginToken", "==", loginToken)
      .limit(1)
      .get();

    if (snapshot.empty) {
      return res.status(404).json({ error: "Documento de login não encontrado." });
    }

    const docRef = snapshot.docs[0].ref;
    const loginData = snapshot.docs[0].data();

    const createdAt = loginData.createdAt?.toDate?.() || loginData.createdAt;
    const now = new Date();
    const diffInSeconds = (now.getTime() - createdAt.getTime()) / 1000;
    const attempts = loginData.attempts || 0;
    //const newAttempts = attempts + 1

    await docRef.update({ attempts: attempts + 1 });

    if (diffInSeconds > 60 || attempts >= 3) {
      await docRef.delete();
      return res.status(403).json({
        error: diffInSeconds > 60 ? "Token expirado" : "Tentativas excedidas"
      });
    }


    const uid = loginData?.uid;
    if (!uid) {
      return res.status(404).json({ uid: null });
    }

    return res.status(200).json({ uid });
  })().catch(error => {
    console.error("Erro interno:", error);
    res.status(500).json({ error: "Erro interno no servidor." });
  });
});