// functions/src/utils/validators.js
exports.isPositiveNumber = (v) => {
  const n = Number(v);
  return !Number.isNaN(n) && n > 0;
};

exports.trimOrNull = (v) => (v ? String(v).trim() : null);

exports.isPhoneReasonable = (s) => {
  if (!s) return false;
  const str = String(s).replace(/\s+/g, "");
  // very basic length check; tweak if you want stricter rules
  return str.length >= 8 && str.length <= 20;
};
