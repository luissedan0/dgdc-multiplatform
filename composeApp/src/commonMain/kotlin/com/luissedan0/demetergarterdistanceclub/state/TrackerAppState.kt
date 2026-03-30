package com.luissedan0.demetergarterdistanceclub.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.luissedan0.demetergarterdistanceclub.model.DayLog
import com.luissedan0.demetergarterdistanceclub.model.WeekBlock
import com.luissedan0.demetergarterdistanceclub.storage.TrackerStorage

enum class AppTab {
    HOME,
    PROFILE
}

@Stable
class TrackerAppState(
    private val storage: TrackerStorage
) {
    var activeHowlerName by mutableStateOf(storage.activeHowlerName)
        private set

    var loginDraft by mutableStateOf(activeHowlerName.orEmpty())
        private set

    var loginGoalDraft by mutableStateOf("")
        private set

    var currentTab by mutableStateOf(AppTab.HOME)
        private set

    var goalDraft by mutableStateOf(storage.goalMiles?.formatForInput().orEmpty())
        private set

    var goalMiles by mutableStateOf(storage.goalMiles)
        private set

    var showDeleteDataConfirmation by mutableStateOf(false)
        private set

    private val dayLogsState = mutableStateListOf<DayLog>().apply {
        addAll(storage.loadDayLogs())
    }

    val isLoggedIn: Boolean
        get() = !activeHowlerName.isNullOrBlank()

    val totalMileage: Double
        get() = dayLogsState.sumOf { it.milesText.toDoubleOrNull() ?: 0.0 }

    val remainingMiles: Double
        get() = (goalMiles ?: 0.0) - totalMileage

    val completionPercent: Double
        get() = when {
            goalMiles == null || goalMiles == 0.0 -> 0.0
            else -> (totalMileage / goalMiles!!) * 100.0
        }

    val weekBlocks: List<WeekBlock>
        get() = listOf(
            WeekBlock("Week 1", dayLogsState.subList(0, 7)),
            WeekBlock("Week 2", dayLogsState.subList(7, 14)),
            WeekBlock("Week 3", dayLogsState.subList(14, 21)),
            WeekBlock("Week 4", dayLogsState.subList(21, 28)),
            WeekBlock("Week 5", dayLogsState.subList(28, 30))
        )

    fun updateLoginDraft(value: String) {
        loginDraft = value
    }

    fun login() {
        val trimmed = loginDraft.trim()
        if (trimmed.isBlank()) return
        val pendingLoginGoal = loginGoalDraft

        activeHowlerName = trimmed
        storage.saveActiveHowlerName(trimmed)
        reloadUserData()
        if (pendingLoginGoal.isNotBlank()) {
            goalDraft = pendingLoginGoal
            loginGoalDraft = pendingLoginGoal
            saveGoal()
        }
        currentTab = AppTab.HOME
    }

    fun logout() {
        activeHowlerName = null
        loginDraft = ""
        loginGoalDraft = ""
        goalDraft = ""
        goalMiles = null
        resetDayLogs()
        storage.clearActiveHowlerName()
    }

    fun selectTab(tab: AppTab) {
        currentTab = tab
    }

    fun updateTracked(dayOfMonth: Int, tracked: Boolean) {
        val current = dayLogsState[dayOfMonth - 1]
        val updated = current.copy(tracked = tracked)
        dayLogsState[dayOfMonth - 1] = updated
        storage.saveDayLog(updated)
    }

    fun updateMiles(dayOfMonth: Int, newValue: String) {
        val sanitized = sanitizeMileageInput(newValue)
        val current = dayLogsState[dayOfMonth - 1]
        val updated = current.copy(
            milesText = sanitized,
            tracked = isMileageConsideredLogged(sanitized)
        )
        dayLogsState[dayOfMonth - 1] = updated
        storage.saveDayLog(updated)
    }

    fun updateLoginGoalDraft(value: String) {
        loginGoalDraft = sanitizeMileageInput(value)
    }

    fun updateGoalDraft(value: String) {
        goalDraft = sanitizeMileageInput(value)
    }

    fun saveGoal() {
        goalMiles = goalDraft.toDoubleOrNull()
        storage.saveGoalMiles(goalMiles)
        goalDraft = goalMiles?.formatForInput().orEmpty()
        loginGoalDraft = goalDraft
    }

    fun requestDeleteAllData() {
        showDeleteDataConfirmation = true
    }

    fun dismissDeleteDataConfirmation() {
        showDeleteDataConfirmation = false
    }

    fun confirmDeleteAllData() {
        showDeleteDataConfirmation = false
        loginGoalDraft = ""
        goalDraft = ""
        goalMiles = null
        resetDayLogs()
        storage.clearAllSavedData()
        activeHowlerName?.let(storage::saveActiveHowlerName)
    }

    private fun reloadUserData() {
        goalMiles = storage.goalMiles
        goalDraft = goalMiles?.formatForInput().orEmpty()
        loginGoalDraft = goalDraft
        val loadedLogs = storage.loadDayLogs()
        repeat(dayLogsState.size) { index ->
            dayLogsState[index] = loadedLogs[index]
        }
    }

    private fun resetDayLogs() {
        repeat(dayLogsState.size) { index ->
            dayLogsState[index] = DayLog(dayOfMonth = index + 1)
        }
    }
}

private val mileageRegex = Regex("""^\d*([.]\d{0,2})?$""")

private fun sanitizeMileageInput(value: String): String {
    val trimmed = value.trim()
    return if (trimmed.isEmpty() || mileageRegex.matches(trimmed)) trimmed else trimmed.dropLast(1)
}

internal fun isMileageConsideredLogged(value: String): Boolean {
    val numericValue = value.toDoubleOrNull() ?: return false
    return numericValue != 0.0
}

private fun Double.formatForInput(): String = if (this % 1.0 == 0.0) {
    this.toInt().toString()
} else {
    formatTwoDecimals(this).trimEnd('0').trimEnd('.')
}

fun formatTwoDecimals(value: Double): String = value.asFixed(2)

private fun Double.asFixed(decimals: Int): String {
    val factor = buildDecimalFactor(decimals)
    val rounded = kotlin.math.round(this * factor) / factor
    val whole = rounded.toLong()
    val fraction = kotlin.math.round((rounded - whole) * factor).toLong()
    val fractionText = fraction.toString().padStart(decimals, '0')
    return "$whole.$fractionText"
}

private fun buildDecimalFactor(decimals: Int): Double {
    var factor = 1.0
    repeat(decimals) { factor *= 10.0 }
    return factor
}
