package com.luissedan0.demetergarterdistanceclub.storage

import com.luissedan0.demetergarterdistanceclub.model.DayLog
import com.russhwolf.settings.Settings

class TrackerStorage(
    private val settings: Settings = Settings()
) {
    val activeHowlerName: String?
        get() = settings.getStringOrNull(KEY_ACTIVE_HOWLER_NAME)

    val goalMiles: Double?
        get() = activeHowlerName?.let { userKey ->
            settings.getDoubleOrNull(keyGoalMiles(userKey))
        }

    fun saveActiveHowlerName(howlerName: String) {
        settings.putString(KEY_ACTIVE_HOWLER_NAME, howlerName)
    }

    fun clearActiveHowlerName() {
        settings.remove(KEY_ACTIVE_HOWLER_NAME)
    }

    fun loadDayLogs(): List<DayLog> {
        val userKey = activeHowlerName ?: return defaultDayLogs()
        return (1..30).map { day ->
            DayLog(
                dayOfMonth = day,
                tracked = settings.getBoolean(keyTracked(userKey, day), false),
                milesText = settings.getString(keyMiles(userKey, day), "")
            )
        }
    }

    fun saveDayLog(dayLog: DayLog) {
        val userKey = activeHowlerName ?: return
        settings.putBoolean(keyTracked(userKey, dayLog.dayOfMonth), dayLog.tracked)
        settings.putString(keyMiles(userKey, dayLog.dayOfMonth), dayLog.milesText)
    }

    fun saveGoalMiles(goalMiles: Double?) {
        val userKey = activeHowlerName ?: return
        if (goalMiles == null) {
            settings.remove(keyGoalMiles(userKey))
        } else {
            settings.putDouble(keyGoalMiles(userKey), goalMiles)
        }
    }

    fun clearAllSavedData() {
        val userKey = activeHowlerName ?: return
        settings.remove(keyGoalMiles(userKey))
        (1..30).forEach { day ->
            settings.remove(keyTracked(userKey, day))
            settings.remove(keyMiles(userKey, day))
        }
    }

    private fun defaultDayLogs(): List<DayLog> = (1..30).map { day ->
        DayLog(dayOfMonth = day)
    }

    private fun normalizeUserKey(howlerName: String): String {
        return buildString {
            howlerName.trim().lowercase().forEach { character ->
                when {
                    character.isLetterOrDigit() -> append(character)
                    character.isWhitespace() || character == '-' || character == '_' -> append('_')
                }
            }
        }.trim('_').ifEmpty { "howler" }
    }

    private fun keyGoalMiles(howlerName: String): String = "user_${normalizeUserKey(howlerName)}_goal_miles"
    private fun keyTracked(howlerName: String, day: Int): String = "user_${normalizeUserKey(howlerName)}_day_${day}_tracked"
    private fun keyMiles(howlerName: String, day: Int): String = "user_${normalizeUserKey(howlerName)}_day_${day}_miles"

    private companion object {
        const val KEY_ACTIVE_HOWLER_NAME = "active_howler_name"
    }
}
