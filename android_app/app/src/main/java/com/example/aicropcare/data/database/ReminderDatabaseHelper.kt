package com.example.aicropcare.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.aicropcare.data.models.FarmingReminder
import com.example.aicropcare.data.models.ReminderType
import java.util.Calendar

class ReminderDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val TAG = "ReminderDB"
        private const val DATABASE_NAME = "aicropcare_reminders.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "farming_reminders"
        const val COLUMN_ID = "id"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CROP_NAME = "crop_name"
        const val COLUMN_REMINDER_TYPE = "reminder_type"
        const val COLUMN_REMINDER_TIMESTAMP = "reminder_timestamp"
        const val COLUMN_FORMATTED_DATE = "formatted_date"
        const val COLUMN_FORMATTED_TIME = "formatted_time"
        const val COLUMN_NOTES = "notes"
        const val COLUMN_WEATHER_PRECAUTION = "weather_precaution"
        const val COLUMN_IS_COMPLETED = "is_completed"
        const val COLUMN_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_ID INTEGER NOT NULL,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_CROP_NAME TEXT,
                $COLUMN_REMINDER_TYPE TEXT NOT NULL,
                $COLUMN_REMINDER_TIMESTAMP INTEGER NOT NULL,
                $COLUMN_FORMATTED_DATE TEXT NOT NULL,
                $COLUMN_FORMATTED_TIME TEXT NOT NULL,
                $COLUMN_NOTES TEXT,
                $COLUMN_WEATHER_PRECAUTION TEXT,
                $COLUMN_IS_COMPLETED INTEGER NOT NULL DEFAULT 0,
                $COLUMN_CREATED_AT INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_reminder_user ON $TABLE_NAME ($COLUMN_USER_ID)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_reminder_time ON $TABLE_NAME ($COLUMN_REMINDER_TIMESTAMP ASC)")
        Log.d(TAG, "Table $TABLE_NAME created successfully.")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "Upgrading database from $oldVersion to $newVersion")
    }

    fun insertReminder(reminder: FarmingReminder): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID, reminder.userId)
            put(COLUMN_TITLE, reminder.title)
            put(COLUMN_CROP_NAME, reminder.cropName)
            put(COLUMN_REMINDER_TYPE, reminder.reminderType.name)
            put(COLUMN_REMINDER_TIMESTAMP, reminder.reminderTimestamp)
            put(COLUMN_FORMATTED_DATE, reminder.formattedDate)
            put(COLUMN_FORMATTED_TIME, reminder.formattedTime)
            put(COLUMN_NOTES, reminder.notes)
            put(COLUMN_WEATHER_PRECAUTION, reminder.weatherPrecaution)
            put(COLUMN_IS_COMPLETED, if (reminder.isCompleted) 1 else 0)
            put(COLUMN_CREATED_AT, reminder.createdAt)
        }
        val id = db.insert(TABLE_NAME, null, values)
        Log.d(TAG, "Inserted reminder with id: $id for user: ${reminder.userId}")
        return id
    }

    fun updateReminder(reminder: FarmingReminder): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, reminder.title)
            put(COLUMN_CROP_NAME, reminder.cropName)
            put(COLUMN_REMINDER_TYPE, reminder.reminderType.name)
            put(COLUMN_REMINDER_TIMESTAMP, reminder.reminderTimestamp)
            put(COLUMN_FORMATTED_DATE, reminder.formattedDate)
            put(COLUMN_FORMATTED_TIME, reminder.formattedTime)
            put(COLUMN_NOTES, reminder.notes)
            put(COLUMN_WEATHER_PRECAUTION, reminder.weatherPrecaution)
            put(COLUMN_IS_COMPLETED, if (reminder.isCompleted) 1 else 0)
        }
        val rows = db.update(
            TABLE_NAME,
            values,
            "$COLUMN_ID = ? AND $COLUMN_USER_ID = ?",
            arrayOf(reminder.id.toString(), reminder.userId.toString())
        )
        return rows > 0
    }

    fun setReminderCompleted(id: Long, userId: Int, isCompleted: Boolean): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_COMPLETED, if (isCompleted) 1 else 0)
        }
        val rows = db.update(
            TABLE_NAME,
            values,
            "$COLUMN_ID = ? AND $COLUMN_USER_ID = ?",
            arrayOf(id.toString(), userId.toString())
        )
        return rows > 0
    }

    fun deleteReminder(id: Long, userId: Int): Boolean {
        val db = writableDatabase
        val rows = db.delete(
            TABLE_NAME,
            "$COLUMN_ID = ? AND $COLUMN_USER_ID = ?",
            arrayOf(id.toString(), userId.toString())
        )
        return rows > 0
    }

    fun getReminderById(id: Long, userId: Int): FarmingReminder? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_ID = ? AND $COLUMN_USER_ID = ?",
            arrayOf(id.toString(), userId.toString()),
            null,
            null,
            null
        )
        return cursor.use {
            if (it.moveToFirst()) cursorToReminder(it) else null
        }
    }

    fun getAllReminders(userId: Int): List<FarmingReminder> {
        val list = mutableListOf<FarmingReminder>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_USER_ID = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "$COLUMN_REMINDER_TIMESTAMP ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToReminder(it))
            }
        }
        return list
    }

    fun getTodayReminders(userId: Int): List<FarmingReminder> {
        val (startOfDay, endOfDay) = getTodayBounds()
        val list = mutableListOf<FarmingReminder>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_USER_ID = ? AND $COLUMN_REMINDER_TIMESTAMP >= ? AND $COLUMN_REMINDER_TIMESTAMP <= ?",
            arrayOf(userId.toString(), startOfDay.toString(), endOfDay.toString()),
            null,
            null,
            "$COLUMN_REMINDER_TIMESTAMP ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToReminder(it))
            }
        }
        return list
    }

    fun getUpcomingReminders(userId: Int): List<FarmingReminder> {
        val (_, endOfDay) = getTodayBounds()
        val list = mutableListOf<FarmingReminder>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_USER_ID = ? AND $COLUMN_REMINDER_TIMESTAMP > ? AND $COLUMN_IS_COMPLETED = 0",
            arrayOf(userId.toString(), endOfDay.toString()),
            null,
            null,
            "$COLUMN_REMINDER_TIMESTAMP ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToReminder(it))
            }
        }
        return list
    }

    fun getCompletedReminders(userId: Int): List<FarmingReminder> {
        val list = mutableListOf<FarmingReminder>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_USER_ID = ? AND $COLUMN_IS_COMPLETED = 1",
            arrayOf(userId.toString()),
            null,
            null,
            "$COLUMN_REMINDER_TIMESTAMP DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToReminder(it))
            }
        }
        return list
    }

    fun getTodayPendingCount(userId: Int): Int {
        val (startOfDay, endOfDay) = getTodayBounds()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_NAME WHERE $COLUMN_USER_ID = ? AND $COLUMN_REMINDER_TIMESTAMP >= ? AND $COLUMN_REMINDER_TIMESTAMP <= ? AND $COLUMN_IS_COMPLETED = 0",
            arrayOf(userId.toString(), startOfDay.toString(), endOfDay.toString())
        )
        return cursor.use {
            if (it.moveToFirst()) it.getInt(0) else 0
        }
    }

    private fun getTodayBounds(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis

        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val end = cal.timeInMillis

        return Pair(start, end)
    }

    private fun cursorToReminder(cursor: Cursor): FarmingReminder {
        return FarmingReminder(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
            title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
            cropName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CROP_NAME)),
            reminderType = ReminderType.fromString(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REMINDER_TYPE))),
            reminderTimestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_REMINDER_TIMESTAMP)),
            formattedDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FORMATTED_DATE)),
            formattedTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FORMATTED_TIME)),
            notes = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES)),
            weatherPrecaution = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WEATHER_PRECAUTION)),
            isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1,
            createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))
        )
    }
}
