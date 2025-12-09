// functions/src/routes/products.js
const express = require("express");
const router = express.Router();
const productsController = require("../controllers/products");
const adminAuth = require("../middleware/adminAuth");

// Public: list products (with optional category filter)
router.get("/", productsController.listProducts);

// Public: get product by id
router.get("/:id", productsController.getProduct);

// Admin/dev: create product
router.post("/", adminAuth, productsController.createProduct);

module.exports = router;
