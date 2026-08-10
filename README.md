# Saathi Voice

Saathi is an Android voice assistant app built with Jetpack Compose. It combines voice input, text-to-speech, and scheme discovery workflows to help users interact with the app hands-free.

## Features

- Voice capture with foreground microphone service support
- Text-to-speech output for assistant responses
- Home, conversation, history, profile, privacy, and onboarding screens
- Scheme browsing and eligibility workflows
- Local persistence and repository-backed data handling
- Network integration for assistant and recommendation APIs

## Tech Stack

- Kotlin
- Jetpack Compose
- AndroidX Navigation
- DataStore Preferences
- Ktor, Retrofit, OkHttp
- Google Generative AI integration

## Requirements

- Android Studio latest stable release
- JDK 11
- Android SDK 37 or compatible build tools

## Setup

1. Open the project in Android Studio.
2. Sync Gradle.
3. Add any required API credentials to your local environment or config files as needed.
4. Run the `app` module on an emulator or physical device.

## Notes

- `google-services.json` is included for Firebase/Google services setup.
- The app requests microphone and internet permissions at runtime or install time.
