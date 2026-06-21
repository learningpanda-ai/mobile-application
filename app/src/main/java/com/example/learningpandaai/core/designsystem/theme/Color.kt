package com.example.learningpandaai.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────────────────────
//  Learning Panda — "Calm Teal" palette
//  Vivid, energetic teal primary balanced by a warm coral accent, on a warm
//  cream canvas. Cool/focused where it counts, warm and welcoming overall.
//  Token names are kept stable so every screen reskins automatically.
// ─────────────────────────────────────────────────────────────────────────────

// —— Brand: Teal (vivid primary actions, CTAs, focus) ——
val BrandPrimary = Color(0xFF0FB5A4)             // Vivid, energetic teal
val BrandPrimaryDark = Color(0xFF0B8C7F)         // Pressed / 3D button base
val BrandPrimaryGradientEnd = Color(0xFF14C9A8)  // Hero-card teal gradient end
val BrandPrimaryContainer = Color(0xFFCDF2EE)    // Soft teal wells & chips
val PureWhite = Color(0xFFFFFFFF)

// —— Brand: Coral (warm accent — energetic counterpoint to the teal) ——
val BrandSecondary = Color(0xFFFB7E5B)           // Warm coral
val BrandSecondaryDark = Color(0xFFE0603C)       // Pressed / 3D button base
val BrandSecondaryContainer = Color(0xFFFFE3D7)  // Soft coral wells

// —— Accents (gentle gamification) ——
val AccentAmber = Color(0xFFF5A524)              // Vivid honey — streaks
val AccentAmberContainer = Color(0xFFFCEFD2)     // Soft honey wells
val AccentMint = Color(0xFF1FA873)               // Fresh green — progress
val AccentMintContainer = Color(0xFFD6F2E4)      // Soft green wells

// —— Surfaces (light) ——
val SurfaceBackground = Color(0xFFFAF6F0)           // Warm cream canvas
val SurfaceBackgroundDark = Color(0xFF15201E)       // Cool-warm dark canvas
val SurfaceContainer = Color(0xFFF1ECE4)            // Grouped content / soft cards
val SurfaceContainerLow = Color(0xFFF5F1EA)         // Nested sections
val SurfaceContainerDark = Color(0xFF1F2C29)        // Dark elevated cards
val SurfaceContainerLowDark = Color(0xFF1A2522)     // Nested sections in dark mode
val SurfaceHighlight = Color(0xFFD2F1ED)            // Teal-tinted focus banners
val SurfaceChip = Color(0xFFD2F1ED)                 // Tag and badge backgrounds
val SurfaceBanner = Color(0xFFF1ECE4)               // Neutral hint banners
val SurfaceAvatar = Color(0xFF0FB5A4)               // Avatar / logo circles (teal)

// —— Text (warm ink, not cold black) ——
val TextPrimary = Color(0xFF2A2520)                // Headings & body on light surfaces
val TextSecondary = Color(0xFF786E64)              // Supporting copy & inactive labels
val TextTertiary = Color(0xFFACA194)               // Placeholders, hints
val TextSecondaryDark = Color(0xFFBFC7C2)          // Supporting copy on dark surfaces
val TextOnChip = Color(0xFF0A5E55)                 // Foreground on teal chip surfaces
val TextOnSecondaryContainer = Color(0xFF8A3A1E)   // Foreground on coral surfaces
val TextOnTertiaryContainer = Color(0xFF0F6B47)    // Foreground on green surfaces
val TextOnBanner = Color(0xFF685E54)               // Foreground on banner surfaces

// —— Borders ——
val BorderDefault = Color(0xFFE6DDD2)              // Outlines and social-login borders
val BorderSubtle = Color(0xFFEFE8DE)              // Input fields and dividers

// —— Status ——
val StatusSuccess = Color(0xFF1FA873)             // Positive feedback (fresh green)
val StatusError = Color(0xFFE5534B)               // Validation errors (warm red)
val StatusErrorContainer = Color(0xFFFBE3DF)      // Inline error banner backgrounds

// —— Component tokens ——
val ButtonDark = Color(0xFF2A2520)                 // Professional anchor CTA face
val ButtonDarkPressed = Color(0xFF1A1510)          // Anchor CTA 3D base / pressed
val TextOnDarkButton = PureWhite                   // Foreground on anchor CTAs
val NavInactive = Color(0xFFA89B8E)                // Unselected bottom-tab tint
val ProgressTrack = Color(0xFF33403C)              // Progress bar track on dark surfaces

// —— Subscription plan (premium "Pro" accents — Gemini-style avatar ring + badge) ——
val PlanProRingStart = Color(0xFFF5A524)           // Premium gradient: warm gold
val PlanProRingMid = Color(0xFFFB7E5B)             // Premium gradient: coral
val PlanProRingEnd = Color(0xFF0FB5A4)             // Premium gradient: brand teal
val PlanProBadge = Color(0xFFF5A524)               // Pro chip background (gold)
val PlanProOnBadge = Color(0xFF4A3206)             // Foreground on the gold Pro chip
val PlanFreeRing = Color(0xFFB8AFA4)               // Free-tier avatar ring (muted warm gray)
val PlanFreeBadge = Color(0xFFE6DDD2)              // Free chip background
val PlanFreeOnBadge = Color(0xFF685E54)            // Foreground on the free chip