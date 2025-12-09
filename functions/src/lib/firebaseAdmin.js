// functions/src/lib/firebaseAdmin.js
const admin = require("firebase-admin");

try {
  if (process.env.FIREBASE_SERVICE_ACCOUNT) {
    // When deployed on Render we will pass the whole service account JSON as an env var.
    const serviceAccount = JSON.parse(process.env.FIREBASE_SERVICE_ACCOUNT);
    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount),
    });
  } else {
    // When running inside GCP/Firebase (or locally with GOOGLE_APPLICATION_CREDENTIALS)
    admin.initializeApp();
  }
} catch (e) {
  // ignore "already initialized" or parse errors (we'll log below if needed)
  // console.warn('firebase admin init warning', e);
}

const db = admin.firestore();

module.exports = { admin, db };
