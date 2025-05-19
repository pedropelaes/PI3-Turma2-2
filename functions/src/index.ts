import { onCall } from "firebase-functions/v2/https";
import * as admin from "firebase-admin";
const QRCode = require("qrcode");

admin.initializeApp();

export const checkEmailVerificationV2 = onCall(
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

export const performAuth = onCall(async (request) => {
  const site = (request.data?.site || "").trim().toLowerCase();
  const apiKey = (request.data?.apiKey || "").trim();

  if (!site || !apiKey) {
    throw new Error("Parâmetros 'site' e 'apiKey' são obrigatórios.");
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

  // Gera loginToken de 256 caracteres
  const loginToken = Array.from({ length: 256 }, () =>
    Math.floor(Math.random() * 36).toString(36)
  ).join("");

  // Cria documento na coleção "login"
  await db.collection("login").doc(loginToken).set({
    site,
    apiKey,
    loginToken,
    createdAt: admin.firestore.FieldValue.serverTimestamp()
  });

  // Gera QRCode (base64)
  const qrCodeImage = await QRCode.toDataURL(loginToken);

  return { qrCodeImage }; // Será acessado com .data.qrCodeImage no front-end
});