// --- Core dependencies ---
const express = require("express");
const mysql = require("mysql2/promise");
const cors = require("cors");
const multer = require("multer");

const app = express();
app.use(cors());

// Use multer for multipart/form-data (files)
const storage = multer.memoryStorage();
const upload = multer({ storage });

// Database config
const dbConfig = {
  host: "localhost",
  user: "root",
  password: "",
  database: "parabitinss"
};
const pool = mysql.createPool(dbConfig);
console.log("DB Connected");

async function insertAccommodation(data, files) {
  const sql = `
    INSERT INTO accomodationreg (
      OwnerAddhar, OwnerName, RegMobNo1, RegMobNo2,
      CareTakerAddhar, CareTakerName, CareTakerMob1, CareTakerMob2,
      SamagraID, FSamagraID, AccName, PlotNo, Street, Colony, Landmark,
      City, District, Block, Email, Password, OTPSMS,
      Lattitude, Longitude, Pincode, DistanceFromGZ, Capacity, ChargeType,
      image1, image2, image3, image4, image5,
      ZoneFK, CircleFK
    ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
  `;

  const values = [
    data.OwnerAddhar || null,
    data.OwnerName || null,
    data.RegMobNo1 || null,
    data.RegMobNo2 || null,
    data.CareTakerAddhar || null,
    data.CareTakerName || null,
    data.CareTakerMob1 || null,
    data.CareTakerMob2 || null,
    data.SamagraID || null,
    data.FSamagraID || null,
    data.AccName || null,
    data.PlotNo || null,
    data.Street || null,
    data.Colony || null,
    data.Landmark || null,
    data.City || null,
    data.District || null,
    data.Block || null,
    data.Email || null,
    data.Password || null,   
    data.OTPSMS || null,
    data.Lattitude || null,
    data.Longitude || null,
    data.Pincode || null,
    data.DistanceFromGZ || null,
    data.Capacity || null,
    data.ChargeType || null,
    files?.image1?.[0]?.buffer || null,
    files?.image2?.[0]?.buffer || null,
    files?.image3?.[0]?.buffer || null,
    files?.image4?.[0]?.buffer || null,
    files?.image5?.[0]?.buffer || null,
    data.ZoneFK || null,
    data.CircleFK || null
  ];

  const [result] = await pool.query(sql, values);
  return result;
}

app.post(
  "/registerAccommodation",
  upload.fields([
    { name: "image1" }, { name: "image2" },
    { name: "image3" }, { name: "image4" },
    { name: "image5" }
  ]),
  async (req, res) => {
    try {
      const result = await insertAccommodation(req.body, req.files);
      res.json({ message: "Accommodation registered successfully", id: result.insertId });
    } catch (err) {
      console.error("Error inserting accommodation:", err);
      res.status(500).json({ error: "Failed to register accommodation" });
    }
  }
);

app.listen(3000, () => console.log("✅ Server running on http://localhost:3000"));
