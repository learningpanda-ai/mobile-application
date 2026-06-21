package com.example.learningpandaai.core.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.learningpandaai.core.util.ProfileImageUrlResolver


/**
 * High-performance, hardware-backed secure storage engine.
 * Encrypts and persists all student identity metrics, parent contacts, academic subjects,
 * and socratic learning mindsets directly inside the device Android Keystore.
 */
class SecurePreferences(context: Context) {

    companion object {
        private const val TAG = "SecurePreferences"
        private const val SECURE_PREFS_FILE = "secure_learning_panda_prefs"

        //Encryption keys: Authentication
        private const val KEY_OAUTH_TOKEN = "oauth_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_PROFILE_IMAGE_URL = "profile_image_url"
        private const val KEY_PLAN = "subscription_plan"

        // Encryption keys: Student Personal & Academics (Step 1 & 2)
        private const val KEY_EMAIL = "student_email"
        private const val KEY_FIRST_NAME = "student_first_name"
        private const val KEY_GRADE_LEVEL = "student_grade_level"
        private const val KEY_BOARD = "student_educational_board"

        // Encryption keys: Parent Info
        private const val KEY_PARENT_NAME = "parent_guardian_name"
        private const val KEY_PARENT_MOBILE = "parent_guardian_mobile"
        private const val KEY_PARENT_EMAIL = "parent_guardian_email"

        // Encryption keys: Subjects Selection (Step 3)
        private const val KEY_SELECTED_SUBJECTS = "student_selected_subjects"

        // Encryption keys: Socratic Mindsets (Step 4)
        private const val KEY_FAVORITE_SUBJECT = "student_favorite_subject"
        private const val KEY_STUDIES_FEELING = "student_studies_feeling"
        private const val KEY_CAREER_IDEA = "student_career_idea"
        private const val KEY_DISCOVER_STRENGTHS = "student_discover_strengths"
    }

    private val sharedPreferences: SharedPreferences by lazy {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                SECURE_PREFS_FILE,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize EncryptedSharedPreferences. Re-creating Keystore.", e)
            context.deleteSharedPreferences(SECURE_PREFS_FILE)

            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                SECURE_PREFS_FILE,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }

    }

    // --- Authentication ---
    // Token writes use commit() (synchronous) rather than apply() so a refresh that happens right
    // before a process death or a follow-up request is guaranteed to be on disk before we proceed.

    fun saveOAuthToken(token: String) {
        sharedPreferences.edit().putString(KEY_OAUTH_TOKEN, token).commit()
    }

    fun getOAuthToken(): String? = sharedPreferences.getString(KEY_OAUTH_TOKEN, null)

    fun saveRefreshToken(token: String) {
        sharedPreferences.edit().putString(KEY_REFRESH_TOKEN, token).commit()
    }

    fun getRefreshToken(): String? = sharedPreferences.getString(KEY_REFRESH_TOKEN, null)

    fun clearAuthTokens() {
        sharedPreferences.edit()
            .remove(KEY_OAUTH_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .commit()
    }

    fun saveProfileImageUrl(url: String?) {
        val resolved = ProfileImageUrlResolver.resolve(url)
        sharedPreferences.edit().apply {
            if (resolved.isNullOrBlank()) {
                remove(KEY_PROFILE_IMAGE_URL)
            } else {
                putString(KEY_PROFILE_IMAGE_URL, resolved)
            }
        }.apply()
    }

    fun getProfileImageUrl(): String? =
        ProfileImageUrlResolver.resolve(
            sharedPreferences.getString(KEY_PROFILE_IMAGE_URL, null)
        )

    // --- Subscription plan ---

    fun savePlan(plan: String?) {
        sharedPreferences.edit().apply {
            if (plan.isNullOrBlank()) remove(KEY_PLAN) else putString(KEY_PLAN, plan)
        }.apply()
    }

    fun getPlan(): String? = sharedPreferences.getString(KEY_PLAN, null)

    // --- Student Profile ---

    fun saveStudentProfile(
        firstName: String,
        gradeLevel: String,
        board: String
    ) {
        sharedPreferences.edit().apply {
            putString(KEY_FIRST_NAME, firstName)
            putString(KEY_GRADE_LEVEL, gradeLevel)
            putString(KEY_BOARD, board)
        }.apply()
    }

    fun getFirstName(): String? = sharedPreferences.getString(KEY_FIRST_NAME, null)
    fun getGradeLevel(): String? = sharedPreferences.getString(KEY_GRADE_LEVEL, null)
    fun getBoard(): String? = sharedPreferences.getString(KEY_BOARD, null)

    fun saveEmail(email: String) {
        sharedPreferences.edit().putString(KEY_EMAIL, email).apply()
    }

    fun getEmail(): String? = sharedPreferences.getString(KEY_EMAIL, null)


    // --- Parent Profile ---

    fun saveParentProfile(
        parentName: String,
        parentMobile: String,
        parentEmail: String
    ) {
        sharedPreferences.edit().apply {
            putString(KEY_PARENT_NAME, parentName)
            putString(KEY_PARENT_MOBILE, parentMobile)
            putString(KEY_PARENT_EMAIL, parentEmail)
        }.apply()
    }

    fun getParentName(): String? = sharedPreferences.getString(KEY_PARENT_NAME, null)
    fun getParentMobile(): String? = sharedPreferences.getString(KEY_PARENT_MOBILE, null)
    fun getParentEmail(): String? = sharedPreferences.getString(KEY_PARENT_EMAIL, null)

    // --- Selected Subjects (Step 3) ---
    fun saveSelectedSubjects(subjects: Set<String>) {
        sharedPreferences.edit().putStringSet(KEY_SELECTED_SUBJECTS, subjects).apply()
    }

    fun getSelectedSubjects(): Set<String> {
        return sharedPreferences.getStringSet(KEY_SELECTED_SUBJECTS, emptySet())?.toSet()
            ?: emptySet()
    }

    // --- Socratic Mindsets (Step 4) ---

    fun saveLearningMindset(
        favoriteSub: String,
        feeling: String,
        career: String,
        strengths: String
    ) {
        sharedPreferences.edit().apply {
            putString(KEY_FAVORITE_SUBJECT, favoriteSub)
            putString(KEY_STUDIES_FEELING, feeling)
            putString(KEY_CAREER_IDEA, career)
            putString(KEY_DISCOVER_STRENGTHS, strengths)
        }.apply()
    }

    fun getFavoriteSubject(): String? = sharedPreferences.getString(KEY_FAVORITE_SUBJECT, null)
    fun getStudiesFeeling(): String? = sharedPreferences.getString(KEY_STUDIES_FEELING, null)
    fun getCareerIdea(): String? = sharedPreferences.getString(KEY_CAREER_IDEA, null)
    fun getDiscoverStrengths(): String? = sharedPreferences.getString(KEY_DISCOVER_STRENGTHS, null)

    // --- Data Purge ---

    fun clearAllData() {
        sharedPreferences.edit().clear().apply()
    }

}