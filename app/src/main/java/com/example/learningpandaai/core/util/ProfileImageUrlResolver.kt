package com.example.learningpandaai.core.util

import com.example.learningpandaai.BuildConfig

object ProfileImageUrlResolver {

    fun resolve(raw : String?) : String? {
        val trimmed = raw?.trim()?.takeIf { it.isNotBlank() } ?: return null
        if(trimmed.startsWith("http://", ignoreCase = true) ||
            trimmed.startsWith("https://", ignoreCase = true))
        {
            return trimmed
        }

        val base = BuildConfig.BASE_URL.trimEnd('/')
        val path = if (trimmed.startsWith("/")) trimmed else "/$trimmed"

        return base + path;
    }

}