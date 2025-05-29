import { onRequest } from "firebase-functions/v2/https";
import * as admin from "firebase-admin";
import { Request, Response } from "express";
const QRCode = require("qrcode");

admin.initializeApp();

export const checkEmailIsVerified = onRequest({ region: "southamerica-east1" }, async (req, res) => {
  const email = (req.body?.data?.email || "").trim().toLowerCase();
  console.log("E-mail recebido:", email);

  if (!email) {
    res.status(400).json({ error: "E-mail não fornecido." });
    return;
  }

  try {
    const userRecord = await admin.auth().getUserByEmail(email);
    res.status(200).json({ data: { verified: userRecord.emailVerified } });
  } catch (error: any) {
    console.error("Erro ao buscar e-mail:", email, error.code);
    if (error.code === "auth/user-not-found") {
      res.status(404).json({ error: "Usuário não encontrado." });
    } else {
      res.status(500).json({ error: "Erro interno ao buscar usuário." });
    }
  }
});

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

    const createdAt = admin.firestore.Timestamp.now();
    const expiresAt = admin.firestore.Timestamp.fromMillis(
      createdAt.toMillis() + 60 * 1000
    )

    await db.collection("login").doc(loginToken).set({
      site,
      apiKey,
      loginToken,
      createdAt: createdAt
    });

    const qrCodeImage = await QRCode.toDataURL(loginToken);
    res.status(200).json({ qrCodeImage: qrCodeImage, loginToken: loginToken, createdAt: createdAt.toMillis(), expiresAt: expiresAt.toMillis() });
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
    const newAttempts = attempts + 1

    const uid = loginData?.uid;
    const accessToken = loginData?.accessToken
    if (uid && accessToken) {
      docRef.update({ status: "aprovado" });
      return res.status(200).json({ uid: uid, accessToken: accessToken });
    }

    await docRef.update({ attempts: newAttempts });

    if (diffInSeconds > 60 || attempts >= 3) {
      await docRef.delete();
      return res.status(403).json({
        error: diffInSeconds > 60 ? "Token expirado" : "Tentativas excedidas"
      });
    }

    return res.status(404).json({ uid:null });
  })().catch(error => {
    console.error("Erro interno:", error);
    res.status(500).json({ error: "Erro interno no servidor." });
  });
});