package com.luissedan0.demetergarterdistanceclub.model

data class DayLog(
    val dayOfMonth: Int,
    val tracked: Boolean = false,
    val milesText: String = ""
)

data class WeekBlock(
    val title: String,
    val entries: List<DayLog>
) {
    val totalMileage: Double
        get() = entries.sumOf { it.milesText.toDoubleOrNull() ?: 0.0 }

    val totalDaysTracked: Int
        get() = entries.count { it.tracked }
}
