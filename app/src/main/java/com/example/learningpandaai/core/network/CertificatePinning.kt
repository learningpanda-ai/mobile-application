package com.example.learningpandaai.core.network

import android.net.Uri
import com.example.learningpandaai.BuildConfig
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient

/**
 * Optional TLS certificate pinning for production HTTPS backends.
 *
 * Configure in `local.properties`:
 * ```
 * CERT_PIN_SHA256=base64hash1,base64hash2
 * CERT_PINNING_ENABLED=true
 * ```
 *
 * Generate a pin: `openssl s_client -connect api.example.com:443 | openssl x509 -pubkey -noout | openssl pkey -pubin -outform der | openssl dgst -sha256 -binary | openssl enc -base64`
 */

object CertificatePinning {

    fun applyIfConfigured(builder: OkHttpClient.Builder) {
        if(!BuildConfig.CERT_PINNING_ENABLED) return

        val host = Uri.parse(BuildConfig.BASE_URL).host?.trim().orEmpty()
        if(host.isEmpty() || host == "10.0.2.2" || host == "localhost" || host == "127.0.0.1")
        {
            return
        }

        val pins = BuildConfig.CERT_PIN_SHA256
            .split(',')
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if(pins.isEmpty()) return

        val pinnerBuilder = CertificatePinner.Builder()

        pins.forEach { pin->
            val normalized = if(pin.startsWith("sha256/")) pin else "sha256/$pin"
            pinnerBuilder.add(host,normalized)
        }

        builder.certificatePinner(pinnerBuilder.build())
    }

}