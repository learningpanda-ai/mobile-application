package com.example.learningpandaai.core.util

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * Derives weekly check-ins and month-grid streak marks from streak API fields
 * (`last_activity_date` + `current_streak`) when per-day history is unavailable.
 */
object StreakCalendarMarks {

    data class WeeklyDay(
        val date: LocalDate,
        val dayShort: String,
        val dateShort: String,
        val fullLabel: String,
        val isActive: Boolean
    )

    enum class DayMark {
        BLANK,
        INACTIVE,
        STREAK,
        TODAY,
        TODAY_STREAK,
        FUTURE
    }

    data class MonthCell(
        val dayOfMonth: Int?,
        val mark: DayMark
    )

    fun streakRange(lastActivityDate: String?, currentStreak: Int): ClosedRange<LocalDate>? {
        val lastActive = parseIsoDate(lastActivityDate) ?: return null
        if (currentStreak <= 0) return null
        val start = lastActive.minusDays((currentStreak - 1).toLong())
        return start..lastActive
    }

    fun buildWeeklyDays(lastActivityDate: String?, currentStreak: Int): List<WeeklyDay> {
        val today = LocalDate.now()
        val range = streakRange(lastActivityDate, currentStreak)
        val shortDateFmt = DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())
        val fullDateFmt = DateTimeFormatter.ofPattern("EEEE, d MMM", Locale.getDefault())
        return (6 downTo 0).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong())
            WeeklyDay(
                date = date,
                dayShort = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                dateShort = date.format(shortDateFmt),
                fullLabel = date.format(fullDateFmt),
                isActive = range != null && date in range
            )
        }
    }

    fun buildMonthGrid(
        month: YearMonth,
        lastActivityDate: String?,
        currentStreak: Int
    ): List<MonthCell> {
        val today = LocalDate.now()
        val range = streakRange(lastActivityDate, currentStreak)
        val firstDay = month.atDay(1)
        val leadingBlanks = firstDay.dayOfWeek.value % 7
        val cells = mutableListOf<MonthCell>()
        repeat(leadingBlanks) {
            cells += MonthCell(dayOfMonth = null, mark = DayMark.BLANK)
        }
        for (day in 1..month.lengthOfMonth()) {
            val date = month.atDay(day)
            val mark = when {
                date.isAfter(today) -> DayMark.FUTURE
                date == today && range != null && date in range -> DayMark.TODAY_STREAK
                date == today -> DayMark.TODAY
                range != null && date in range -> DayMark.STREAK
                else -> DayMark.INACTIVE
            }
            cells += MonthCell(dayOfMonth = day, mark = mark)
        }
        return cells
    }

    fun weekDayHeaders(): List<String> = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")

    fun monthTitle(month: YearMonth): String =
        month.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))

    private fun parseIsoDate(iso: String?): LocalDate? =
        iso?.take(10)?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
}
