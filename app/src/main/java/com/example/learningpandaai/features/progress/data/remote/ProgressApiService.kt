package com.example.learningpandaai.features.progress.data.remote

import retrofit2.http.GET

/**
 * Progress analytics endpoints. When the backend route is not deployed yet,
 * calls will return HTTP 404 and the Progress screen error fallback will show.
 */
interface ProgressApiService {

    @GET("api/v1/user/progress")
    suspend fun getProgressStats(): ProgressStatsDto
}
