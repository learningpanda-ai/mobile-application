package com.example.learningpandaai.core.network

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import retrofit2.HttpException
import java.io.IOException

object ApiErrorMapper {

    const val SESSION_EXPIRED_MESSAGE = "Your session expired. Please sign in again."
    const val SESSION_EXPIRED_TITLE = "Session expired"
    const val NETWORK_ERROR_MESSAGE = "No internet connection. Please check your network and try again."
    const val SERVER_ERROR_MESSAGE = "Something went wrong on our side. Please try again shortly."
    const val VALIDATION_ERROR_MESSAGE = "Please check your details and try again."

    fun mapHttpException(exception: HttpException): Exception = when (exception.code()) {
        401 -> SessionExpiredException(SESSION_EXPIRED_MESSAGE, exception)
        403 -> Exception("You don't have access to this feature yet.", exception)
        404 -> Exception("We couldn't find what you're looking for. Please try again.", exception)
        422 -> Exception(parseValidationDetail(exception) ?: VALIDATION_ERROR_MESSAGE, exception)
        in 500..599 -> Exception(SERVER_ERROR_MESSAGE, exception)
        else -> Exception("Unable to complete the request (${exception.code()}). Please try again.", exception)
    }

    fun mapThrowable(throwable: Throwable, fallbackMessage: String): Exception = when (throwable) {
        is SessionExpiredException -> throwable
        is HttpException -> mapHttpException(throwable)
        is IOException -> IOException(NETWORK_ERROR_MESSAGE, throwable)
        else -> Exception(throwable.message ?: fallbackMessage, throwable)
    }

    private fun parseValidationDetail(exception: HttpException): String? {
        val raw = exception.response()?.errorBody()?.string()?.trim().orEmpty()
        if (raw.isEmpty()) return null
        return try {
            val detail = JsonParser.parseString(raw).asJsonObject.get("detail") ?: return null
            when {
                detail.isJsonArray -> formatValidationArray(detail.asJsonArray)
                detail.isJsonPrimitive -> detail.asString.trim().takeIf { it.isNotEmpty() }
                else -> null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun formatValidationArray(array: JsonArray): String? {
        val lines = array.mapNotNull { element -> formatValidationItem(element) }
        return lines.joinToString("\n").takeIf { it.isNotBlank() }
    }

    private fun formatValidationItem(element: JsonElement): String? {
        if (!element.isJsonObject) return null
        val obj = element.asJsonObject
        val msg = obj.get("msg")?.asString?.trim().orEmpty()
        if (msg.isEmpty()) return null
        val field = obj.getAsJsonArray("loc")
            ?.mapNotNull { it.asString.trim().takeIf { part -> part.isNotEmpty() && part != "body" } }
            ?.lastOrNull()
        return if (field != null) "${humanizeField(field)}: $msg" else msg
    }

    private fun humanizeField(field: String): String = when (field) {
        "first_name" -> "First name"
        "last_name" -> "Last name"
        "parent_guardian_mobile" -> "Parent mobile"
        "parent_guardian_email" -> "Parent email"
        "parent_guardian_name" -> "Parent name"
        "city_town" -> "City"
        "grade" -> "Class"
        "board" -> "Board"
        else -> field.replace('_', ' ').replaceFirstChar { it.uppercase() }
    }

}