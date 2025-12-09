// server.js
const app = require("./functions/src/app");

const port = process.env.PORT || 10000;
app.listen(port, () => {
  console.log(`Cozy Creations backend listening on port ${port}`);
});
