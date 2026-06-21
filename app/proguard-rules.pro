# Preserve line numbers in crash stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# RETROFIT — keep API interface methods
-keepattributes Signature
-keepattributes Exceptions
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

# OKHTTP
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# GSON — keep JSON DTO classes
-keepattributes *Annotation*
-keep class com.example.learningpandaai.features.**.data.remote.** { *; }
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# KOTLIN COROUTINES
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# KOTLIN METADATA
-keepattributes RuntimeVisibleAnnotations
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**

# ENCRYPTED PREFS (tokens)
-keep class androidx.security.crypto.** { *; }
-dontwarn androidx.security.crypto.**