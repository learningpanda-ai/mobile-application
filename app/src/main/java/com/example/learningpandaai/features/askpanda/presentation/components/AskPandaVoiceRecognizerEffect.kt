package com.example.learningpandaai.features.askpanda.presentation.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Starts and stops platform speech recognition when [isListening] changes.
 * Call from Voice / Panda Chat modes; ViewModel remains the source of truth for UI state.
 */
@Composable
fun AskPandaVoiceRecognizerEffect(
    isListening: Boolean,
    onPartialResult: (String) -> Unit,
    onFinalResult: (String) -> Unit,
    onError: (String) -> Unit,
    onPermissionDenied: () -> Unit
) {
    val context = LocalContext.current
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasAudioPermission = granted
        if (!granted) onPermissionDenied()
    }

    val recognizerAvailable = remember {
        SpeechRecognizer.isRecognitionAvailable(context)
    }

    val speechRecognizer = remember {
        if (recognizerAvailable) SpeechRecognizer.createSpeechRecognizer(context) else null
    }

    DisposableEffect(speechRecognizer) {
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) = Unit
            override fun onBeginningOfSpeech() = Unit
            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() = Unit
            override fun onEvent(eventType: Int, params: Bundle?) = Unit

            override fun onPartialResults(partialResults: Bundle?) {
                val text = partialResults?.extractBestText() ?: return
                if (text.isNotBlank()) onPartialResult(text)
            }

            override fun onResults(results: Bundle?) {
                val text = results?.extractBestText().orEmpty()
                if (text.isNotBlank()) onFinalResult(text)
            }

            override fun onError(error: Int) {
                if (error == SpeechRecognizer.ERROR_NO_MATCH ||
                    error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT
                ) {
                    return
                }
                onError(recognitionErrorMessage(error))
            }
        }
        speechRecognizer?.setRecognitionListener(listener)
        onDispose {
            speechRecognizer?.setRecognitionListener(null)
            speechRecognizer?.destroy()
        }
    }

    fun startListening() {
        val recognizer = speechRecognizer ?: run {
            onError("Speech recognition is not available on this device.")
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        recognizer.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        speechRecognizer?.cancel()
    }

    LaunchedEffect(isListening, hasAudioPermission) {
        if (!isListening) {
            stopListening()
            return@LaunchedEffect
        }
        if (!hasAudioPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return@LaunchedEffect
        }
        if (!recognizerAvailable) {
            onError("Speech recognition is not available on this device.")
            return@LaunchedEffect
        }
        startListening()
    }
}

private fun Bundle.extractBestText(): String? =
    getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()

private fun recognitionErrorMessage(error: Int): String = when (error) {
    SpeechRecognizer.ERROR_AUDIO -> "Could not capture audio. Try again."
    SpeechRecognizer.ERROR_CLIENT -> "Voice input was interrupted."
    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is required."
    SpeechRecognizer.ERROR_NETWORK -> "Network error during voice input."
    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Voice input timed out. Check your connection."
    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Voice input is busy. Try again in a moment."
    SpeechRecognizer.ERROR_SERVER -> "Voice service error. Try again later."
    else -> "Could not understand that. Tap the mic and try again."
}
