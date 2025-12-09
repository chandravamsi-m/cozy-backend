const EXPECTED_SECRET = process.env.ADMIN_SECRET || "super-secret-token";

module.exports = (req, res, next) => {
  const adminSecret = req.header("x-admin-secret");
  if (adminSecret !== EXPECTED_SECRET) {
    return res.status(401).json({ success: false, message: "Unauthorized" });
  }
  return next();
};
