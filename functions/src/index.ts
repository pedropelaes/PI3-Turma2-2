import { onCall } from "firebase-functions/v2/https";
import * as admin from "firebase-admin";

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