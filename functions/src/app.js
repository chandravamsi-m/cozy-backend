// functions/src/app.js
const express = require("express");
const cors = require("cors");

// require routers (your existing modular routes)
const productsRoutes = require("./routes/products");
const enquiriesRoutes = require("./routes/enquiries");
const miscRoutes = require("./routes/misc");

// allowed origins from env (comma separated) or fallback to localhost dev
// const allowed = (process.env.ALLOWED_ORIGINS || "http://localhost:5174").split(",");

const app = express();

app.use(cors({ origin: true }));

app.use(express.json());

// health route
app.get("/api/health", (req, res) =>
  res.json({ status: "ok", message: "Cozy Creations backend is running 🚀" })
);

// mount modular routers
app.use("/api/products", productsRoutes);
app.use("/api/enquiries", enquiriesRoutes);
app.use("/api", miscRoutes);

module.exports = app;
