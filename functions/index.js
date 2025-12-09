// functions/index.js
const functions = require("firebase-functions");
const app = require("./src/app");

// export for Firebase Functions (if you decide to deploy to Cloud Functions)
// this file is used only when running `firebase deploy --only functions`
exports.api = functions.https.onRequest(app);
