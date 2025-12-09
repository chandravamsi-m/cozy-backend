// functions/src/routes/enquiries.js
const express = require("express");
const router = express.Router();
const enquiriesController = require("../controllers/enquiries");
const adminAuth = require("../middleware/adminAuth");

// Public: create an enquiry
router.post("/", enquiriesController.createEnquiry);

// Admin/dev: list enquiries (requires x-admin-secret header)
router.get("/", adminAuth, enquiriesController.listEnquiries);

// Admin/dev: update enquiry status
router.patch("/:id", adminAuth, enquiriesController.updateEnquiry);

module.exports = router;
