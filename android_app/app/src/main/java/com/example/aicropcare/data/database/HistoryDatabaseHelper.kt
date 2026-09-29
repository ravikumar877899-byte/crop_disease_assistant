package com.example.aicropcare.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.aicropcare.data.models.ScanHistoryRecord

class HistoryDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val TAG = "HistoryDB"
        private const val DATABASE_NAME = "aicropcare_history.db"
        private const val DATABASE_VERSION = 4

        const val TABLE_NAME = "scan_history"
        const val COLUMN_ID = "id"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_CROP_NAME = "crop_name"
        const val COLUMN_DISEASE_NAME = "disease_name"
        const val COLUMN_CONFIDENCE = "confidence"
        const val COLUMN_AFFECTED_PERCENTAGE = "affected_percentage"
        const val COLUMN_SEVERITY = "severity"
        const val COLUMN_RISK_LEVEL = "risk_level"
        const val COLUMN_RISK_REASON = "risk_reason"
        const val COLUMN_IMMEDIATE_ACTION = "immediate_action"
        const val COLUMN_RESCAN_RECOMMENDED = "rescan_recommended"
        const val COLUMN_SYMPTOMS = "symptoms"
        const val COLUMN_TREATMENT = "treatment"
        const val COLUMN_TREATMENT_PLAN_JSON = "treatment_plan_json"
        const val COLUMN_IS_HEALTHY = "is_healthy"
        const val COLUMN_IS_CLEAR = "is_clear"
        const val COLUMN_ENGINE = "engine"
        const val COLUMN_IMAGE_PATH = "image_path"
        const val COLUMN_TIMESTAMP = "timestamp"
        const val COLUMN_FORMATTED_DATE = "formatted_date"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_ID INTEGER,
                $COLUMN_USERNAME TEXT,
                $COLUMN_CROP_NAME TEXT NOT NULL,
                $COLUMN_DISEASE_NAME TEXT NOT NULL,
                $COLUMN_CONFIDENCE REAL,
                $COLUMN_AFFECTED_PERCENTAGE INTEGER,
                $COLUMN_SEVERITY TEXT DEFAULT 'UNKNOWN',
                $COLUMN_RISK_LEVEL TEXT DEFAULT 'UNKNOWN',
                $COLUMN_RISK_REASON TEXT DEFAULT '',
                $COLUMN_IMMEDIATE_ACTION TEXT DEFAULT '',
                $COLUMN_RESCAN_RECOMMENDED INTEGER DEFAULT 0,
                $COLUMN_SYMPTOMS TEXT,
                $COLUMN_TREATMENT TEXT,
                $COLUMN_TREATMENT_PLAN_JSON TEXT DEFAULT '',
                $COLUMN_IS_HEALTHY INTEGER,
                $COLUMN_IS_CLEAR INTEGER,
                $COLUMN_ENGINE TEXT,
                $COLUMN_IMAGE_PATH TEXT,
                $COLUMN_TIMESTAMP INTEGER,
                $COLUMN_FORMATTED_DATE TEXT
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_user_id ON $TABLE_NAME ($COLUMN_USER_ID)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_timestamp ON $TABLE_NAME ($COLUMN_TIMESTAMP DESC)")
        Log.d(TAG, "Database and table $TABLE_NAME created successfully.")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "Upgrading database from $oldVersion to $newVersion")
        if (oldVersion < 3) {
            try {
                db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_SEVERITY TEXT DEFAULT 'UNKNOWN'")
            } catch (e: Exception) { Log.w(TAG, "Column $COLUMN_SEVERITY upgrade notice", e) }
            try {
                db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_RISK_LEVEL TEXT DEFAULT 'UNKNOWN'")
            } catch (e: Exception) { Log.w(TAG, "Column $COLUMN_RISK_LEVEL upgrade notice", e) }
            try {
                db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_RISK_REASON TEXT DEFAULT ''")
            } catch (e: Exception) { Log.w(TAG, "Column $COLUMN_RISK_REASON upgrade notice", e) }
            try {
                db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_IMMEDIATE_ACTION TEXT DEFAULT ''")
            } catch (e: Exception) { Log.w(TAG, "Column $COLUMN_IMMEDIATE_ACTION upgrade notice", e) }
            try {
                db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_RESCAN_RECOMMENDED INTEGER DEFAULT 0")
            } catch (e: Exception) { Log.w(TAG, "Column $COLUMN_RESCAN_RECOMMENDED upgrade notice", e) }
        }
        if (oldVersion < 4) {
            try {
                db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_TREATMENT_PLAN_JSON TEXT DEFAULT ''")
            } catch (e: Exception) { Log.w(TAG, "Column $COLUMN_TREATMENT_PLAN_JSON upgrade notice", e) }
        }
    }

    fun insertScan(record: ScanHistoryRecord): Long {
        return try {
            val db = writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_USER_ID, record.userId)
                put(COLUMN_USERNAME, record.username)
                put(COLUMN_CROP_NAME, record.cropName)
                put(COLUMN_DISEASE_NAME, record.diseaseName)
                put(COLUMN_CONFIDENCE, record.confidence)
                put(COLUMN_AFFECTED_PERCENTAGE, record.affectedPercentage)
                put(COLUMN_SEVERITY, record.severity)
                put(COLUMN_RISK_LEVEL, record.riskLevel)
                put(COLUMN_RISK_REASON, record.riskReason)
                put(COLUMN_IMMEDIATE_ACTION, record.immediateAction)
                put(COLUMN_RESCAN_RECOMMENDED, if (record.rescanRecommended) 1 else 0)
                put(COLUMN_SYMPTOMS, record.symptoms)
                put(COLUMN_TREATMENT, record.treatment)
                put(COLUMN_TREATMENT_PLAN_JSON, record.treatmentPlanJson)
                put(COLUMN_IS_HEALTHY, if (record.isHealthy) 1 else 0)
                put(COLUMN_IS_CLEAR, if (record.isClear) 1 else 0)
                put(COLUMN_ENGINE, record.engine)
                put(COLUMN_IMAGE_PATH, record.imagePath)
                put(COLUMN_TIMESTAMP, record.timestamp)
                put(COLUMN_FORMATTED_DATE, record.formattedDate)
            }
            val rowId = db.insert(TABLE_NAME, null, values)
            Log.d(TAG, "Successfully inserted scan record ID=$rowId for crop='${record.cropName}', disease='${record.diseaseName}'")
            rowId
        } catch (e: Exception) {
            Log.e(TAG, "Failed to insert scan record into SQLite", e)
            -1L
        }
    }

    fun getHistoryForUser(userId: Int = -1, username: String? = null): List<ScanHistoryRecord> {
        val records = mutableListOf<ScanHistoryRecord>()
        try {
            val db = readableDatabase
            // Always retrieve all scans saved on this local device, sorted newest first
            val cursor: Cursor = db.query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                "$COLUMN_TIMESTAMP DESC"
            )

            cursor.use {
                val idCol = it.getColumnIndex(COLUMN_ID)
                val userIdCol = it.getColumnIndex(COLUMN_USER_ID)
                val usernameCol = it.getColumnIndex(COLUMN_USERNAME)
                val cropCol = it.getColumnIndex(COLUMN_CROP_NAME)
                val diseaseCol = it.getColumnIndex(COLUMN_DISEASE_NAME)
                val confCol = it.getColumnIndex(COLUMN_CONFIDENCE)
                val affectedCol = it.getColumnIndex(COLUMN_AFFECTED_PERCENTAGE)
                val severityCol = it.getColumnIndex(COLUMN_SEVERITY)
                val riskLevelCol = it.getColumnIndex(COLUMN_RISK_LEVEL)
                val riskReasonCol = it.getColumnIndex(COLUMN_RISK_REASON)
                val immediateActionCol = it.getColumnIndex(COLUMN_IMMEDIATE_ACTION)
                val rescanCol = it.getColumnIndex(COLUMN_RESCAN_RECOMMENDED)
                val symptomsCol = it.getColumnIndex(COLUMN_SYMPTOMS)
                val treatmentCol = it.getColumnIndex(COLUMN_TREATMENT)
                val treatmentPlanCol = it.getColumnIndex(COLUMN_TREATMENT_PLAN_JSON)
                val isHealthyCol = it.getColumnIndex(COLUMN_IS_HEALTHY)
                val isClearCol = it.getColumnIndex(COLUMN_IS_CLEAR)
                val engineCol = it.getColumnIndex(COLUMN_ENGINE)
                val imagePathCol = it.getColumnIndex(COLUMN_IMAGE_PATH)
                val timestampCol = it.getColumnIndex(COLUMN_TIMESTAMP)
                val dateCol = it.getColumnIndex(COLUMN_FORMATTED_DATE)

                while (it.moveToNext()) {
                    records.add(
                        ScanHistoryRecord(
                            id = if (idCol >= 0) it.getLong(idCol) else 0L,
                            userId = if (userIdCol >= 0) it.getInt(userIdCol) else -1,
                            username = if (usernameCol >= 0) it.getString(usernameCol) ?: "" else "",
                            cropName = if (cropCol >= 0) it.getString(cropCol) ?: "Unknown Crop" else "Unknown Crop",
                            diseaseName = if (diseaseCol >= 0) it.getString(diseaseCol) ?: "Healthy" else "Healthy",
                            confidence = if (confCol >= 0) it.getFloat(confCol) else 0f,
                            affectedPercentage = if (affectedCol >= 0) it.getInt(affectedCol) else 0,
                            severity = if (severityCol >= 0) it.getString(severityCol) ?: "UNKNOWN" else "UNKNOWN",
                            riskLevel = if (riskLevelCol >= 0) it.getString(riskLevelCol) ?: "UNKNOWN" else "UNKNOWN",
                            riskReason = if (riskReasonCol >= 0) it.getString(riskReasonCol) ?: "" else "",
                            immediateAction = if (immediateActionCol >= 0) it.getString(immediateActionCol) ?: "" else "",
                            rescanRecommended = if (rescanCol >= 0) it.getInt(rescanCol) == 1 else false,
                            symptoms = if (symptomsCol >= 0) it.getString(symptomsCol) ?: "" else "",
                            treatment = if (treatmentCol >= 0) it.getString(treatmentCol) ?: "" else "",
                            treatmentPlanJson = if (treatmentPlanCol >= 0) it.getString(treatmentPlanCol) ?: "" else "",
                            isHealthy = if (isHealthyCol >= 0) it.getInt(isHealthyCol) == 1 else true,
                            isClear = if (isClearCol >= 0) it.getInt(isClearCol) == 1 else true,
                            engine = if (engineCol >= 0) it.getString(engineCol) ?: "Gemini Vision AI" else "Gemini Vision AI",
                            imagePath = if (imagePathCol >= 0) it.getString(imagePathCol) else null,
                            timestamp = if (timestampCol >= 0) it.getLong(timestampCol) else 0L,
                            formattedDate = if (dateCol >= 0) it.getString(dateCol) ?: "" else ""
                        )
                    )
                }
            }
            Log.d(TAG, "getHistoryForUser loaded ${records.size} records from SQLite")
        } catch (e: Exception) {
            Log.e(TAG, "Error querying scan history from SQLite", e)
        }
        return records
    }

    fun getScanById(id: Long): ScanHistoryRecord? {
        try {
            val db = readableDatabase
            val cursor = db.query(
                TABLE_NAME,
                null,
                "$COLUMN_ID = ?",
                arrayOf(id.toString()),
                null,
                null,
                null
            )

            cursor.use {
                if (it.moveToFirst()) {
                    val idCol = it.getColumnIndex(COLUMN_ID)
                    val userIdCol = it.getColumnIndex(COLUMN_USER_ID)
                    val usernameCol = it.getColumnIndex(COLUMN_USERNAME)
                    val cropCol = it.getColumnIndex(COLUMN_CROP_NAME)
                    val diseaseCol = it.getColumnIndex(COLUMN_DISEASE_NAME)
                    val confCol = it.getColumnIndex(COLUMN_CONFIDENCE)
                    val affectedCol = it.getColumnIndex(COLUMN_AFFECTED_PERCENTAGE)
                    val severityCol = it.getColumnIndex(COLUMN_SEVERITY)
                    val riskLevelCol = it.getColumnIndex(COLUMN_RISK_LEVEL)
                    val riskReasonCol = it.getColumnIndex(COLUMN_RISK_REASON)
                    val immediateActionCol = it.getColumnIndex(COLUMN_IMMEDIATE_ACTION)
                    val rescanCol = it.getColumnIndex(COLUMN_RESCAN_RECOMMENDED)
                    val symptomsCol = it.getColumnIndex(COLUMN_SYMPTOMS)
                    val treatmentCol = it.getColumnIndex(COLUMN_TREATMENT)
                    val treatmentPlanCol = it.getColumnIndex(COLUMN_TREATMENT_PLAN_JSON)
                    val isHealthyCol = it.getColumnIndex(COLUMN_IS_HEALTHY)
                    val isClearCol = it.getColumnIndex(COLUMN_IS_CLEAR)
                    val engineCol = it.getColumnIndex(COLUMN_ENGINE)
                    val imagePathCol = it.getColumnIndex(COLUMN_IMAGE_PATH)
                    val timestampCol = it.getColumnIndex(COLUMN_TIMESTAMP)
                    val dateCol = it.getColumnIndex(COLUMN_FORMATTED_DATE)

                    return ScanHistoryRecord(
                        id = if (idCol >= 0) it.getLong(idCol) else id,
                        userId = if (userIdCol >= 0) it.getInt(userIdCol) else -1,
                        username = if (usernameCol >= 0) it.getString(usernameCol) ?: "" else "",
                        cropName = if (cropCol >= 0) it.getString(cropCol) ?: "Unknown Crop" else "Unknown Crop",
                        diseaseName = if (diseaseCol >= 0) it.getString(diseaseCol) ?: "Healthy" else "Healthy",
                        confidence = if (confCol >= 0) it.getFloat(confCol) else 0f,
                        affectedPercentage = if (affectedCol >= 0) it.getInt(affectedCol) else 0,
                        severity = if (severityCol >= 0) it.getString(severityCol) ?: "UNKNOWN" else "UNKNOWN",
                        riskLevel = if (riskLevelCol >= 0) it.getString(riskLevelCol) ?: "UNKNOWN" else "UNKNOWN",
                        riskReason = if (riskReasonCol >= 0) it.getString(riskReasonCol) ?: "" else "",
                        immediateAction = if (immediateActionCol >= 0) it.getString(immediateActionCol) ?: "" else "",
                        rescanRecommended = if (rescanCol >= 0) it.getInt(rescanCol) == 1 else false,
                        symptoms = if (symptomsCol >= 0) it.getString(symptomsCol) ?: "" else "",
                        treatment = if (treatmentCol >= 0) it.getString(treatmentCol) ?: "" else "",
                        treatmentPlanJson = if (treatmentPlanCol >= 0) it.getString(treatmentPlanCol) ?: "" else "",
                        isHealthy = if (isHealthyCol >= 0) it.getInt(isHealthyCol) == 1 else true,
                        isClear = if (isClearCol >= 0) it.getInt(isClearCol) == 1 else true,
                        engine = if (engineCol >= 0) it.getString(engineCol) ?: "Gemini Vision AI" else "Gemini Vision AI",
                        imagePath = if (imagePathCol >= 0) it.getString(imagePathCol) else null,
                        timestamp = if (timestampCol >= 0) it.getLong(timestampCol) else 0L,
                        formattedDate = if (dateCol >= 0) it.getString(dateCol) ?: "" else ""
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching scan by id: $id", e)
        }
        return null

    }

    fun deleteScan(id: Long): String? {
        return try {
            val record = getScanById(id)
            val imagePath = record?.imagePath
            val db = writableDatabase
            val rowsDeleted = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
            Log.d(TAG, "deleteScan ID=$id deleted $rowsDeleted row(s)")
            imagePath
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting scan ID=$id", e)
            null
        }
    }

    fun clearAllForUser(userId: Int = -1, username: String? = null): List<String> {
        val records = getHistoryForUser(userId, username)
        val imagePaths = records.mapNotNull { it.imagePath }
        try {
            val db = writableDatabase
            val rowsDeleted = db.delete(TABLE_NAME, null, null)
            Log.d(TAG, "clearAll deleted $rowsDeleted scan history row(s)")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing all scan history", e)
        }
        return imagePaths
    }
}
