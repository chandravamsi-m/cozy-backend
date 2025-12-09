// functions/src/routes/misc.js
const express = require("express");
const router = express.Router();
const miscController = require("../controllers/misc");

router.get("/customization-options", miscController.getCustomizationOptions);
router.get("/site-config", miscController.getSiteConfig);

module.exports = router;
