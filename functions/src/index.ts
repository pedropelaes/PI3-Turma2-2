/**
 * Import function triggers from their respective submodules:
 *
 * import {onCall} from "firebase-functions/v2/https";
 * import {onDocumentWritten} from "firebase-functions/v2/firestore";
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

// import {onRequest} from "firebase-functions/v2/https";
// import * as logger from "firebase-functions/logger";

// Start writing functions
// https://firebase.google.com/docs/functions/typescript

// export const helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

admin.initializeApp();

export const checkEmailVerification = functions.https.onCall(
  async (data: any, context) => {
    console.log("Recebido pela função:", JSON.stringify(data));

    if (!data || typeof data.email !== "string") {
      console.log("Erro: data.email ausente ou inválido");
      throw new functions.https.HttpsError("invalid-argument", "E-mail não fornecido.");
    }

    const email = data.email.trim().toLowerCase();

    if (!email) {
      console.log("E-mail ausente ou vazio:", data);
      throw new functions.https.HttpsError("invalid-argument", "E-mail não fornecido.");
    }

    try {
      const userRecord = await admin.auth().getUserByEmail(email);
      return { verified: userRecord.emailVerified };
    } catch (error: any) {
      throw new functions.https.HttpsError("not-found", error.message);
    }
  }
);