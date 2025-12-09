// functions/src/controllers/enquiries.js
const { db } = require("../lib/firebaseAdmin");
const { isPositiveNumber, trimOrNull, isPhoneReasonable } = require("../utils/validators");

exports.createEnquiry = async (req, res) => {
  try {
    const payload = req.body;

    const quantityNum = Number(payload.quantity);

    if (
      !payload.name ||
      !payload.phone ||
      !isPositiveNumber(quantityNum) ||
      (!payload.productId && !payload.productName)
    ) {
      return res.status(400).json({
        success: false,
        message:
          "Missing or invalid fields: name, phone, quantity (> 0), and either productId or productName.",
      });
    }

    const cleanedName = trimOrNull(payload.name);
    const cleanedPhone = trimOrNull(payload.phone);
    const cleanedEmail = trimOrNull(payload.email);
    const cleanedProductName = trimOrNull(payload.productName);
    const cleanedCustomization = trimOrNull(payload.customizationRequest) || "";
    const cleanedDeliveryLocation = trimOrNull(payload.deliveryLocation) || "";

    if (!isPhoneReasonable(cleanedPhone)) {
      return res.status(400).json({
        success: false,
        message: "Phone number seems invalid or too short/long.",
      });
    }

    const enquiryData = {
      name: cleanedName,
      email: cleanedEmail,
      phone: cleanedPhone,
      productId: payload.productId || null,
      productName: cleanedProductName,
      productCategory: payload.productCategory || null,
      quantity: quantityNum,
      customizationRequest: cleanedCustomization,
      deliveryLocation: cleanedDeliveryLocation,
      source: payload.source || "website",
      status: "new",
      createdAt: new Date(),
    };

    const docRef = await db.collection("enquiries").add(enquiryData);

    return res.status(201).json({
      success: true,
      message: "Enquiry submitted successfully",
      id: docRef.id,
    });
  } catch (error) {
    console.error("createEnquiry error:", error);
    return res.status(500).json({
      success: false,
      message: "Failed to submit enquiry",
    });
  }
};

exports.listEnquiries = async (req, res) => {
  try {
    const snapshot = await db
      .collection("enquiries")
      .orderBy("createdAt", "desc")
      .limit(50)
      .get();

    const enquiries = snapshot.docs.map((doc) => ({ id: doc.id, ...doc.data() }));

    return res.json({
      success: true,
      count: enquiries.length,
      data: enquiries,
    });
  } catch (error) {
    console.error("listEnquiries error:", error);
    return res.status(500).json({
      success: false,
      message: "Failed to fetch enquiries",
    });
  }
};

exports.updateEnquiry = async (req, res) => {
  try {
    const { id } = req.params;
    const { status } = req.body;

    const validStatuses = ["new", "contacted", "completed", "cancelled"];

    if (!status || !validStatuses.includes(status)) {
      return res.status(400).json({
        success: false,
        message: `Invalid status. Allowed: ${validStatuses.join(", ")}`,
      });
    }

    const docRef = db.collection("enquiries").doc(id);
    const doc = await docRef.get();

    if (!doc.exists) {
      return res.status(404).json({
        success: false,
        message: "Enquiry not found",
      });
    }

    await docRef.update({ status, updatedAt: new Date() });

    return res.json({
      success: true,
      message: "Enquiry updated successfully",
    });
  } catch (error) {
    console.error("updateEnquiry error:", error);
    return res.status(500).json({
      success: false,
      message: "Failed to update enquiry",
    });
  }
};
