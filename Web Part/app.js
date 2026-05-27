// --- Core dependencies ---
const express = require('express');                                // HTTP server & routing
const mysql = require('mysql2/promise');
const cors = require('cors');                                       // Cross-Origin Resource Sharing
const multer = require('multer');                                   // Multipart/form-data (file uploads)


//insiliuzation
const app = express();
// Enable CORS for browser clients calling this API
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));


// We keep files in memory as Buffers (saves disk I/O, but watch RAM usage).
// For production, consider: limits + fileFilter + external object storage.
const storage = multer.memoryStorage();
const upload = multer({ storage });

const dbConfig = {
    host: 'localhost',
    user: 'root',
    password: '',
    database: 'parabitinss'
};

const pool = mysql.createPool(dbConfig);
console.log("DB Connected");

// ============================
// HELPER & ROUTE
// ============================

async function insertPerson(memberData) {
    const sql = `
        INSERT INTO personreg (
            Name, MobNo, AadharNo, Gender, Nationality, EmergencyMob, Age,
            ModeOfTransport, TransportNo, HouseNo, Colony, TownCity, Tehsil, Block, District,
            Pincode, StateFK, Landmark, PoliceStation, PanchyatNo, IdentificationMark,
            TravellingDate, ReturnDate, ReturnType, ATypeFK, RegTypeFK, Photo, GroupID
        ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
    `;
    const values = [
        memberData.Name || null, memberData.MobNo || null, memberData.AadharNo || null,
        memberData.Gender || null, memberData.Nationality || null, memberData.EmergencyMob || null,
        memberData.Age || null, memberData.ModeOfTransport || null, memberData.TransportNo || null,
        memberData.HouseNo || null, memberData.Colony || null, memberData.TownCity || null,
        memberData.Tehsil || null, memberData.Block || null, memberData.District || null,
        memberData.Pincode || null, memberData.StateFK || null, memberData.Landmark || null,
        memberData.PoliceStation || null, memberData.PanchyatNo || null, memberData.IdentificationMark || null,
        memberData.TravellingDate || null, memberData.ReturnDate || null, memberData.ReturnType || null,
        memberData.ATypeFK || null, memberData.RegTypeFK || null, memberData.Photo || null,
        memberData.GroupID || null
    ];
    console.log(`Inserting person: ${memberData.Name}`);
    const [result] = await pool.query(sql, values);
    if (result.affectedRows === 0) {
        throw new Error(`Insert failed for member: ${memberData.Name || 'Unknown'}`);
    }
    return result;
}

app.post('/insert-person', upload.any(), async (req, res, next) => {
    try {
        if (req.body.commonDetails) {
            // =============================================================
            // ▼▼▼ FINAL FIX: The logic is now much simpler! ▼▼▼
            // =============================================================

            const commonDetails = JSON.parse(req.body.commonDetails);
            const groupIdToUse = req.body.GroupID;
            const membersFromRequest = req.body.members; // Access the array directly

            if (!membersFromRequest || !Array.isArray(membersFromRequest) || membersFromRequest.length === 0) {
                return res.status(400).json({ error: "No member array found in the request." });
            }

            const insertPromises = membersFromRequest.map((memberData, index) => {
                // Find the photo for this member by matching the index
                // Match photo by convention: fieldname = members[0][Photo], members[1][Photo], ...
                const photoFile = req.files.find(f => f.fieldname === `members[${index}][Photo]`);

                // Combine common details with unique member details
                const finalMember = {
                    ...commonDetails,
                    ...memberData, // This contains Name, MobNo, AadharNo, etc.
                    GroupID: groupIdToUse,
                    Photo: photoFile ? photoFile.buffer : null
                };
                return insertPerson(finalMember);
            });

            await Promise.all(insertPromises);
            res.json({ message: "All members inserted successfully!", groupId: groupIdToUse });

            
            
        

        } else {
            // This part for single-person registration remains the same and is correct.
            const isNewGroup = req.body.isNewGroup === 'true';
            let groupIdToUse = null;

            if (isNewGroup) {
                const [rows] = await pool.query("SELECT MAX(GroupID) AS maxId FROM personreg");
                groupIdToUse = (rows[0].maxId || 0) + 1;
            }

            const memberData = {
                ...req.body,
                Photo: req.files.find(f => f.fieldname === 'Photo')?.buffer || null,
                GroupID: groupIdToUse
            };

            await insertPerson(memberData);
            res.json({ message: "Person inserted successfully!", groupId: groupIdToUse });
        }
    } catch (err) {
        next(err);
    }
});

// 
app.use((err, req, res, next) => {
    console.error("💥 An error was caught by the global handler:", err);
    if (err instanceof multer.MulterError) {
        return res.status(422).json({ error: "File Upload Error", details: err.message });
    }
    return res.status(500).json({ error: "An internal server error occurred", details: err.message });
});


// START SERVER

app.listen(3000, () => {
    console.log('Server running on localhost:3000');
});