package com.example.learningpandaai.core.util

import com.example.learningpandaai.features.progress.domain.DailyActivityHours

/**
 * Derives a rolling 7-day check-in view from streak API fields when no progress analytics route exists.
 */
object StreakWeekMarks {

    fun buildWeeklyCheckIns(lastActivityDate: String?, currentStreak: Int): List<DailyActivityHours> {
        return StreakCalendarMarks.buildWeeklyDays(lastActivityDate, currentStreak).map { day ->
            DailyActivityHours(
                dayLabel = day.dayShort,
                hours = if (day.isActive) 1f else 0f
            )
        }
    }
}
