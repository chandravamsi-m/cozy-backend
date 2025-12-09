// functions/src/controllers/misc.js

exports.getCustomizationOptions = (req, res) => {
  const options = {
    colors: [
      "white",
      "ivory",
      "pastel pink",
      "lavender",
      "baby blue",
      "sage green",
      "peach",
    ],
    fragrances: [
      "vanilla",
      "lavender",
      "rose",
      "jasmine",
      "sandalwood",
      "ocean breeze",
      "citrus",
    ],
    packaging: [
      "no gift wrap",
      "basic gift wrap",
      "premium gift box",
      "hamper box",
      "return gift pack",
    ],
    occasions: [
      "birthday",
      "wedding",
      "housewarming",
      "festival",
      "corporate gifting",
      "thank you gift",
    ],
  };

  return res.json({ success: true, data: options });
};

exports.getSiteConfig = (req, res) => {
  const config = {
    brandName: "Cozy Creations",
    tagline: "Crafted with Love, Made for Happy Hearts",
    contact: {
      email: "cozycandlecorner13@gmail.com",
      phone: "8019401322",
      whatsAppNumber: "918019401322",
      addressLines: "Gajularamaram, Hyderabad",
      city: "Hyderabad",
      state: "Telangana",
      country: "India",
    },
    social: {
      instagramHandle: "@cozycreationscandle",
      instagramUrl: "https://www.instagram.com/cozycreationscandle",
    },
    hero: {
      headline: "Handcrafted Scented Candles for Every Cozy Moment",
      subheadline:
        "Customizable fragrances, colors and gift-ready packaging for your special occasions.",
    },
  };

  return res.json({ success: true, data: config });
};
