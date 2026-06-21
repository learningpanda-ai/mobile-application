package com.example.learningpandaai.features.progress.data

import com.example.learningpandaai.core.data.SecurePreferences
import com.example.learningpandaai.core.network.ApiErrorMapper
import com.example.learningpandaai.core.util.Logger
import com.example.learningpandaai.core.util.StreakWeekMarks
import com.example.learningpandaai.features.profile.data.remote.StreakApiService
import com.example.learningpandaai.features.profile.data.remote.StreakDto
import com.example.learningpandaai.features.progress.domain.ProgressRepository
import com.example.learningpandaai.features.progress.domain.StreakStats
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressRepositoryImpl @Inject constructor(
    private val streakApiService: StreakApiService,
    private val securePreferences: SecurePreferences
) : ProgressRepository {

    override suspend fun fetchCurrentStats(): Result<StreakStats> {
        return try {
            val streak = streakApiService.getStreak()
            Logger.d(
                "fetchCurrentStats: success — streak=${streak.currentStreak}, " +
                        "totalActiveDays=${streak.totalActiveDays}"
            )
            Result.success(streak.toStreakStats())
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Logger.e("fetchCurrentStats: network failure — ${e.message}", e)
            Result.failure(IOException("No internet connection. Please check your network.", e))
        } catch (e: HttpException) {
            Logger.e("fetchCurrentStats: HTTP ${e.code()} — ${e.message()}", e)
            Result.failure(ApiErrorMapper.mapHttpException(e))
        } catch (e: Exception) {
            Logger.e("fetchCurrentStats: unexpected error — ${e.message}", e)
            Result.failure(ApiErrorMapper.mapThrowable(e, "Failed to load progress stats. Please retry."))
        }
    }

    override suspend fun recordDailyActivity(): Result<StreakStats> {
        return try {
            val streak = streakApiService.checkIn()
            Logger.d("recordDailyActivity: check-in recorded — streak=${streak.currentStreak}")
            Result.success(streak.toStreakStats())
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Logger.e("recordDailyActivity: network failure — ${e.message}", e)
            Result.failure(IOException("No internet connection. Please check your network.", e))
        } catch (e: HttpException) {
            Logger.e("recordDailyActivity: HTTP ${e.code()} — ${e.message()}", e)
            Result.failure(ApiErrorMapper.mapHttpException(e))
        } catch (e: Exception) {
            Logger.e("recordDailyActivity: unexpected error — ${e.message}", e)
            Result.failure(ApiErrorMapper.mapThrowable(e, "Failed to record activity. Please retry."))
        }
    }

    private fun StreakDto.toStreakStats() = StreakStats(
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        lastActivityDate = lastActivityDate,
        totalActiveDays = totalActiveDays,
        courses = securePreferences.getSelectedSubjects().toList(),
        weeklyActivityHours = StreakWeekMarks.buildWeeklyCheckIns(lastActivityDate, currentStreak)
    )
}
