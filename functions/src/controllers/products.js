// functions/src/controllers/products.js
const { db } = require("../lib/firebaseAdmin");

exports.listProducts = async (req, res) => {
  try {
    const { category } = req.query;

    let productsRef = db.collection("products");
    let query = productsRef.where("isActive", "==", true);

    if (category) {
      query = query.where("category", "==", category);
    }

    const snapshot = await query.get();

    const products = snapshot.docs.map((doc) => ({
      id: doc.id,
      ...doc.data(),
    }));

    return res.json({
      success: true,
      count: products.length,
      data: products,
    });
  } catch (error) {
    console.error("listProducts error:", error);
    return res.status(500).json({
      success: false,
      message: "Failed to fetch products",
    });
  }
};

exports.getProduct = async (req, res) => {
  try {
    const { id } = req.params;

    if (!id) {
      return res.status(400).json({
        success: false,
        message: "Product id is required",
      });
    }

    const docRef = db.collection("products").doc(id);
    const doc = await docRef.get();

    if (!doc.exists) {
      return res.status(404).json({
        success: false,
        message: "Product not found",
      });
    }

    const product = {
      id: doc.id,
      ...doc.data(),
    };

    return res.json({
      success: true,
      data: product,
    });
  } catch (error) {
    console.error("getProduct error:", error);
    return res.status(500).json({
      success: false,
      message: "Failed to fetch product",
    });
  }
};

exports.createProduct = async (req, res) => {
  try {
    const {
      name,
      category,
      waxType,
      weightGrams,
      burnTimeHours,
      price,
      quantityPack,
      customizableFragrance,
      customizableColor,
      imageUrl,
      altText,
    } = req.body;

    // Basic required fields
    if (!name || !category || !waxType || price === undefined || !imageUrl) {
      return res.status(400).json({
        success: false,
        message:
          "Missing required fields: name, category, waxType, price, imageUrl.",
      });
    }

    // Validate category & waxType
    const validCategories = [
      "flower",
      "animal",
      "festive",
      "special",
      "glassJar",
    ];
    const validWaxTypes = ["soy", "gel"];

    if (!validCategories.includes(category)) {
      return res.status(400).json({
        success: false,
        message: `Invalid category. Allowed: ${validCategories.join(", ")}`,
      });
    }

    if (!validWaxTypes.includes(waxType)) {
      return res.status(400).json({
        success: false,
        message: `Invalid waxType. Allowed: ${validWaxTypes.join(", ")}`,
      });
    }

    // Convert numbers safely
    const weightNum = weightGrams !== undefined ? Number(weightGrams) : null;
    const burnTimeNum =
      burnTimeHours !== undefined ? Number(burnTimeHours) : null;
    const priceNum = Number(price);
    const quantityPackNum =
      quantityPack !== undefined ? Number(quantityPack) : 1;

    if (Number.isNaN(priceNum) || priceNum <= 0) {
      return res.status(400).json({
        success: false,
        message: "price must be a positive number.",
      });
    }

    if (weightNum !== null && (Number.isNaN(weightNum) || weightNum <= 0)) {
      return res.status(400).json({
        success: false,
        message: "weightGrams must be a positive number if provided.",
      });
    }

    if (
      burnTimeNum !== null &&
      (Number.isNaN(burnTimeNum) || burnTimeNum <= 0)
    ) {
      return res.status(400).json({
        success: false,
        message: "burnTimeHours must be a positive number if provided.",
      });
    }

    // Build product object
    const productData = {
      name: name.trim(),
      category,
      waxType,
      weightGrams: weightNum,
      burnTimeHours: burnTimeNum,
      price: priceNum,
      quantityPack: quantityPackNum,
      customizableFragrance: !!customizableFragrance,
      customizableColor: !!customizableColor,
      imageUrl,
      altText: altText ? altText.trim() : null,
      isActive: true,
      createdAt: new Date(),
    };

    const docRef = await db.collection("products").add(productData);

    return res.status(201).json({
      success: true,
      message: "Product created successfully",
      id: docRef.id,
    });
  } catch (error) {
    console.error("createProduct error:", error);
    return res.status(500).json({
      success: false,
      message: "Failed to create product",
    });
  }
};
